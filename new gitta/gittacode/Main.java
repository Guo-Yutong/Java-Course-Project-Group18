package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Jiarui Li
 */
public class Main {

    /** Current Working Directory. */
    static final File CWD = new File(".");

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.print("Please enter a command.");
            System.exit(0);
        }

        try {
            switch (args[0]) {
            case "init":
                init(args);
                break;
            case "add":
                add(args);
                break;
            case "commit":
                commit(args);
                break;
            case "rm":
                rm(args);
                break;
            case "log":
                log(args);
                break;
            case "global-log":
                globalLog(args);
                break;
            case "find":
                find(args);
                break;
            case "status":
                status(args);
                break;
            case "checkout":
                checkout(args);
                break;
            case "branch":
                branch(args);
                break;
            case "rm-branch":
                rmBranch(args);
                break;
            case "reset":
                reset(args);
                break;
            case "merge":
                merge(args);
                break;
            default:
                System.out.print("No command with that name exists.");
            }
        } catch (GitletException e) {
            System.out.print(e.getMsg());
        }
        System.exit(0);
    }

    /**
     * Init command.
     * @param args input arguments
     */
    private static void init(String[] args) {
        operandsCheck(args, 1, 0);
        validateRepoExist(false);
        Repository.init();
    }

    /**
     * Add command.
     * @param args input arguments
     */
    private static void add(String[] args) {
        operandsCheck(args, 1, 1);
        validateRepoExist(true);
        Repository.add(validateFileExist(args, 1));
    }

    /**
     * Commit command.
     * @param args input arguments
     */
    private static void commit(String[] args) {
        operandsCheck(args, 2, 0);
        validateRepoExist(true);
        validateStageEmpty();
        validateMSG(args[1]);
        Repository.commit(args[1], null);
    }

    /**
     * Rm command.
     * @param args input argumetns
     */
    private static void rm(String[] args) {
        operandsCheck(args, 2, 0);
        validateRepoExist(true);
        Repository.rm(Utils.join(CWD, args[1]));
    }

    /**
     * Find command.
     * @param args input arguments
     */
    private static void find(String[] args) {
        operandsCheck(args, 2, 0);
        validateRepoExist(true);
        Repository.find(args[1]);
    }

    /**
     * Branch command.
     * @param args input arguments
     */
    private static void branch(String[] args) {
        validateRepoExist(true);
        operandsCheck(args, 2, 0);
        Repository.branch(args[1]);
    }

    /**
     * rm-branch command.
     * @param args input arguments
     */
    private static void rmBranch(String[] args) {
        validateRepoExist(true);
        operandsCheck(args, 2, 0);
        Repository.removeBranch(args[1]);
    }

    /**
     * Reset command.
     * @param args input arguments
     */
    private static void reset(String[] args) {
        validateRepoExist(true);
        operandsCheck(args, 2, 0);
        Repository.reset(args[1]);
    }

    /**
     * Merge command.
     * @param args input arguments
     */
    private static void merge(String[] args) {
        validateRepoExist(true);
        operandsCheck(args, 2, 0);
        Repository.merge(args[1]);
    }

    /**
     * Log command.
     * @param args input arguments
     */
    private static void log(String[] args) {
        operandsCheck(args, 1, 0);
        validateRepoExist(true);
        Repository.log();
    }

    /**
     * Global log command.
     * @param args input arguments
     */
    private static void globalLog(String[] args) {
        operandsCheck(args, 1, 0);
        validateRepoExist(true);
        Repository.printAllLog();
    }

    /**
     * Status command.
     * @param args input arguments
     */
    private static void status(String[] args) {
        operandsCheck(args, 1, 0);
        validateRepoExist(true);
        Repository.status();
    }

    /**
     * Checkout command.
     * @param args input arguments
     */
    private static void checkout(String[] args) {
        if (args.length == 3 && args[1].equals("--")) {
            validateRepoExist(true);
            Repository.checkoutFile(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            validateRepoExist(true);
            Repository.checkoutFileWithID(args[3], args[1]);
        } else {
            operandsCheck(args, 2, 0);
            validateRepoExist(true);
            Repository.checkoutBranch(args[1]);
        }
    }

    /**
     * Check input operands.
     * @param args input arguments
     * @param expected expected operandes num
     * @param type =0 equal to, >0 greater than or equal, <0 less than or equal
     */
    private static void operandsCheck(String[] args, int expected, int type) {
        if ((type > 0 && args.length <= expected)
            || (type == 0 && args.length != expected)
            || (type < 0 && args.length >= expected)) {
            System.out.print("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * Validate if repo exist.
     * @param expected true if validate exists, otherwise validata not exists.
     */
    private static void validateRepoExist(boolean expected) {
        if (Repository.isExisted() != expected) {
            if (expected) {
                System.out.print("Not in an initialized Gitlet directory");
                System.exit(0);
            } else {
                System.out.print("A Gitlet version-control system"
                    + "already exists in the current directory.");
                System.exit(0);
            }
        }
    }

    /** Validate staging area is empty. */
    private static void validateStageEmpty() {
        if (Stage.getStage().isEmpty()) {
            System.out.print("No changes added to the commit.");
            System.exit(0);
        }
    }

    /**
     * Validate commit message.
     * @param msg input commit message
     */
    private static void validateMSG(String msg) {
        assert msg != null;
        if (msg.trim().equals("")) {
            System.out.print("Please enter a commit message");
            System.exit(0);
        }
    }

    /**
     * Validate if file exists.
     * @param args input arguments
     * @param offset arguments offset
     * @return list of existed file
     */
    private static List<File> validateFileExist(String[] args, int offset) {
        ArrayList<File> files = new ArrayList<>();
        for (int i = offset; i < args.length; i += 1) {
            File f = Utils.join(CWD, args[i]);
            if (!f.exists()) {
                System.out.print("File does not exist.");
                System.exit(0);
            }
            files.add(f);
        }
        return files;
    }

}
