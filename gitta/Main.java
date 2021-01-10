package gitta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /** Current Working Directory. */
    static final File PWD = new File(".");

    /** Usage: java gitta.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.print("Please enter a command.\n");
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
                System.out.print("No command with that name exists.\n");
            }
        } catch (GittaException e) {
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
        Repository.rm(GittaUtils.join(PWD, args[1]));
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
            System.out.println("Incorrect operands.\n");
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
                System.out.println("Not in an initialized Gitta directory\n");
                System.exit(0);
            } else {
                System.out.println("A Gitta version-control system"
                    + "already exists in the current directory.\n");
                System.exit(0);
            }
        }
    }

    /** Validate staging area is empty. */
    private static void validateStageEmpty() {
        if (Stage.getStage().isEmpty()) {
            System.out.println("No changes added to the commit.\n");
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
            System.out.println("Please enter a commit message\n");
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
            File f = GittaUtils.join(PWD, args[i]);
            if (!f.exists()) {
                System.out.println("File does not exist.\n");
                System.exit(0);
            }
            files.add(f);
        }
        return files;
    }
    


}
