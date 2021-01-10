package gitta;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Repository {

    /** Current Working Directory. */
    private static final File PWD = new File(".");

    /** Folder that repo live in. */
    public static final File REPO_FOLDER = GittaUtils.join(PWD, ".gitta");

    /** Path that head file live in. */
    private static final File HEAD_PATH = GittaUtils.join(REPO_FOLDER, "head");

    /** Reference info of current repository. */
    private static Reference _reference = Reference.getReference();

    /** Head branch name of current repository. */
    private static String _head = getHead();

    /** Last commit of current repository. */
    private static Commit _lastCommit =
                            Commit.getCommit(_reference.getBranch(_head));

    /** Staging area of current repository. */
    private static Stage _stage = Stage.getStage();

    /**
     * If repository exited.
     * @return true if repo exists
     */
    public static boolean isExisted() {
        return REPO_FOLDER.exists();
    }

    /** Init a new repository. */
    public static void init() throws GittaException {
        assert !isExisted();

        if (!REPO_FOLDER.mkdir()) {
            throw new GittaException("Failed to init repository.\n");
        }
        Stage.init();
        Reference.init();
        GittaIO.writeContents(HEAD_PATH, "master");
    }

    /**
     * Add files into staging area. Assuming files all exists
     * @param files list of added files
     */
    public static void add(List<File> files) {
        for (File file : files) {
        	if (file.isFile()) {
	            String sha1 = new Blob(file).getSHA1();
	            if (_lastCommit.containsFile(file)
	                && _lastCommit.getObjectId(file).equals(sha1)) {
	                if (_stage.isFileStaged(file)) {
	                    _stage.unstagedFile(file);
	                }
	                if (_stage.isFileRemoved(file)) {
	                    _stage.unremovedFile(file);
	                }
	            } else {
	                _stage.addFile(file);
	                _stage.save();
	            }
        	}else if (file.isDirectory()) {
        		String sha1 = new Tree(file).getSHA1();
	            if (_lastCommit.containsFile(file)
	                && _lastCommit.getObjectId(file).equals(sha1)) {
	                if (_stage.isFileStaged(file)) {
	                    _stage.unstagedFile(file);
	                }
	                if (_stage.isFileRemoved(file)) {
	                    _stage.unremovedFile(file);
	                }
	            } else {
	                _stage.addFile(file);
	                _stage.save();
	            }
	            
	            List<File> subsFile = GittaUtils.plainFilesIn(file);
	            add(subsFile);
        	}
        }
    }

    /**
     * Commit files in staging area.
     * @param msg initial commit message
     * @param secondParentId initial second parent id
     */
    public static void commit(String msg, String secondParentId) {
        Commit commit;
        if (secondParentId == null) {
            commit = new Commit(_lastCommit,
                                _reference.getBranch(_head), msg);
        } else {
            commit = new Commit(_lastCommit,
                                _reference.getBranch(_head),
                                secondParentId, msg);
        }
        for (String name : _stage.getStagedFileNames()) {
            commit.commitFile(name, _stage.getObject(name));
        }
        for (String filename : _stage.getRemovedFiles()) {
            commit.removeFile(filename);
        }
        _stage.clear();
        String commitId = commit.saveFile();
        _reference.branchUpdate(_head, commitId);
    }

    /**
     * Get current branch name.
     * @return current branch name
     */
    private static String getHead() {
        if (!REPO_FOLDER.exists()) {
            return null;
        }
        return GittaIO.readContentsAsString(HEAD_PATH);
    }

    /**
     * Save current branch name.
     * @param head head branch name
     */
    private static void saveHead(String head) {
        GittaIO.writeContents(HEAD_PATH, head);
    }

    /**
     * Try remove a file from staging area. If not exists in staging area,
     * then try remove it from working directory.
     * @param file file removed
     * @throws GittaException
     */
    public static void rm(File file) throws GittaException {
        if (_stage.isFileStaged(file)) {
            _stage.unstagedFile(file);
        } else if (_lastCommit.containsFile(file)) {
            _stage.addRemovedFile(file);
            if (!removeFile(file)) {
                throw new GittaException("Failed to remove file"
                                        + "from working directory\n");
            }
        } else {
            throw new GittaException("No reason to remove the file\n");
        }
    }

    /**
     * Remove file from current working directory.
     * @param file file
     * @return true if successfully removed
     */
    private static boolean removeFile(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    /** Print current branch's log. */
    public static void log() {
        String commitID = _reference.getBranch(_head);
        Commit commit = Commit.getCommit(commitID);
        while (commit != null) {
            commit.printLog(commitID);
            commitID = commit.getParentId();
            commit = Commit.getCommit(commitID);
        }
    }

    /** Print all commit's log. */
    public static void printAllLog() {
        Commit.printAllLog();
    }

    /** Find all commits' id with message equals to MSG. */
    public static void find(String msg) {
        List<String> commitIds = Commit.find(msg);
        if (commitIds.isEmpty()) {
            throw new GittaException("Found no commit with that message\n");
        }
        for (String id : commitIds) {
            System.out.println(id);
        }
    }

    /** Print current repository's status. */
    public static void status() {
        printStatus("Branches", _reference.getBranchNames());
        System.out.println();
        System.out.println();
        printStatus("Staged Files", _stage.getStagedFileNames());
        System.out.println();
        System.out.println();
        printStatus("Removed Files", _stage.getRemovedFileNames());
        System.out.println();
        System.out.println();
        printStatus("Modifications Not Staged For Commit",
                    getModifiedFileNames());
        System.out.println();
        System.out.println();
        printStatus("Untracked Files", getUntrackedFileNames());
        System.out.println();
    }

    /**
     * Print one status type according to TYPE.
     * @param type type of status
     * @param status list of status
     */
    private static void printStatus(String type, List<String> status) {
        System.out.print("=== " + type + " ===");
        Collections.sort(status);
        for (String name : status) {
            if (type.equals("Branches") && name.equals(_head)) {
                System.out.println();
                System.out.print("*" + name);
            } else {
                System.out.println();
                System.out.print(name);
            }
        }
    }

    /** Return untracked file names list. */
    private static List<String> getUntrackedFileNames() {
        List<String> untrackedFiles = new ArrayList<>();
        for (String name : GittaUtils.plainFilenamesIn(PWD)) {
        	if (name.contentEquals(".gitta")) {
        	}else if (!_stage.isFileStaged(name)
                    && !_lastCommit.containsFile(name)) {
                untrackedFiles.add(name);
            } else if (_stage.isFileRemoved(name)) {
                untrackedFiles.add(name);
            }
        }
        return untrackedFiles;
    }

    /** Return unmodified file names list. */
    private static List<String> getModifiedFileNames() {
        List<String> modifiedFiles = new ArrayList<>();
        for (String name : GittaUtils.plainFilenamesIn(PWD)) {
        	if(name.contentEquals(".gitta")) {	
        	}else {
	        	File file = GittaUtils.join(PWD, name);
	        	String sha1 = "";
	        	if(file.isFile()) {
		            sha1 = new Blob(file).getSHA1();
	        	} else if (file.isDirectory()) {
	        		sha1 = new Tree(file).getSHA1();
	        	}
	            if (_lastCommit.containsFile(name)
	                    && !_lastCommit.containsObject(sha1)
	                    && !_stage.isFileStaged(name)) {
	                modifiedFiles.add(name + " (modified)");
	            } else if (_stage.isFileStaged(name)
	                    && !_stage.isBlobStaged(name, sha1)) {
	                modifiedFiles.add(name + " (modified");
	            }
        	}
        }
        for (String name : _stage.getStagedFileNames()) {
            if (!GittaUtils.join(PWD, name).exists()) {
                modifiedFiles.add(name + " (deleted)");
            }
        }
        for (String name : _lastCommit.getObjectNames()) {
            if (!GittaUtils.join(PWD, name).exists()
                && !_stage.getRemovedFiles().contains(name)) {
                modifiedFiles.add(name + " (deleted)");
            }
        }
        return modifiedFiles;
    }

    /**
     * Checkout a file.
     * @param filename filename
     * @throws GittaException
     */
    public static void checkoutFile(String filename) throws GittaException {
        if (!_lastCommit.containsFile(filename)) {
            throw new GittaException("File does not exist in that commit\n");
        }
        _lastCommit.restoreFile(GittaUtils.join(PWD, filename));
    }

    /**
     * Checkout a file with corresponding ID.
     * @param filename filename
     * @param id id of file
     * @throws GittaException
     */
    public static void checkoutFileWithID(String filename, String id)
                                                throws GittaException {
        Commit commit = Commit.getCommit(id);
        if (commit == null) {
            throw new GittaException("No commit with that id exists\n");
        }
        if (!commit.containsFile(filename)) {
            throw new GittaException("File does not exist in that commit\n");
        }
        commit.restoreFile(GittaUtils.join(PWD, filename));
    }

    /**
     * Checkout a branch.
     * @param branch branch name
     * @throws GittaException
     */
    public static void checkoutBranch(String branch) throws GittaException {
        if (!_reference.containsBranch(branch)) {
            throw new GittaException("No such branch exists.\n");
        } else if (branch.equals(_head)) {
            throw new GittaException("No need to checkout the current branch\n");
        }
        if (!isValidBranchCheckout(branch)) {
            throw new GittaException("There is an untracked file in the way;"
                                   + " delete it, or add and commit it first\n");
        }
        checkoutCommit(_reference.getBranch(branch));
        saveHead(branch);
    }

    /**
     * Return if is valid checkout to a branch.
     * @param branch branch name
     * @return true if it is valid branch checkout
     */
    private static boolean isValidBranchCheckout(String branch) {
        return isValidCommitCheckout(_reference.getBranch(branch));
    }

    /**
     * Return if is valid checkout to a commit.
     * @param id commit id
     * @return true if it is valid checkout
     */
    private static boolean isValidCommitCheckout(String id) {
        Commit commit = Commit.getCommit(id);
        for (String name : getUntrackedFileNames()) {
            if (commit.containsFile(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checkout current repository to Commit ID. Assuming checkout is valid.
     * @param id commit id
     */
    private static void checkoutCommit(String id) {
        Commit commit = Commit.getCommit(id);
        HashSet<String> filenames = new HashSet<>(commit.getObjectNames());
        for (String name : _lastCommit.getObjectNames()) {
            if (!filenames.contains(name)) {
                GittaUtils.join(PWD, name).delete();
            }
        }
        commit.restoreCommit(PWD);
        _stage.clear();
    }

    /**
     * Create a new branch named BRANCH.
     * @param branch branch name
     * @throws GittaException
     */
    public static void branch(String branch) throws GittaException {
        checkBranchNotExist(branch);
        _reference.addBranch(branch, _reference.getBranch(_head));
    }

    /**
     * Try remove a branch.
     * @param branch branch name
     * @throws GitletException
     */
    public static void removeBranch(String branch) throws GittaException {
        checkBranchExist(branch);
        if (_head.equals(branch)) {
            throw new GittaException("Cannot remove the current branch.\n");
        }
        _reference.removeBranch(branch);
    }

    /**
     * Validate BRANCH existence.
     * @param branch branch name
     */
    private static void checkBranchExist(String branch) {
        if (!_reference.containsBranch(branch)) {
            throw new GittaException("A branch with that name"
                                    + "does not exist.\n");
        }
    }

    /**
     * Validate BRANCH doesn't exist.
     * @param branch branch name
     */
    private static void checkBranchNotExist(String branch) {
        if (_reference.containsBranch(branch)) {
            throw new GittaException("A branch with that name"
                                        + "already exists.\n");
        }
    }

    /**
     * Reset repository to commit ID.
     * @param id commit id
     * @throws GittaException
     */
    public static void reset(String id) throws GittaException {
        Commit commit = Commit.getCommit(id);
        if (commit == null) {
            throw new GittaException("No commit with that id exists\n");
        }
        if (!isValidCommitCheckout(id)) {
            throw new GittaException("There is an untracked file in the way;"
                                  + " delete it, or add and commit it first.\n");
        }
        checkoutCommit(id);
        _reference.branchUpdate(_head, id);
    }

    /**
     * Merge BRANCH into current _HEAD.
     * @param branch branch name
     * @throws GittaException
     */
    public static void merge(String branch) throws GittaException {
        if (!_stage.isEmpty()) {
            throw new GittaException("You have uncommitted changes.\n");
        }
        checkBranchExist(branch);
        validateNotHeadBranch(branch);
        String splitId = findLCA(branch);
        String curId = _reference.getBranch(_head);
        String branchId = _reference.getBranch(branch);
        if (splitId.equals(branchId)) {
            throw new GittaException("Given branch is an ancestor"
                                       + "of the current branch.\n");
        } else if (splitId.equals(curId)) {
            checkoutCommit(branchId);
            throw new GittaException("Current branch fast-forwarded.\n");
        } else {
            handleMerge(curId, branchId, splitId, branch);
        }
    }

    /**
     * Handle regular merge.
     * @param curId current commit id
     * @param branchId given branch's commit id
     * @param splitId split commit id
     * @param branch branch name
     */
    private static void handleMerge(String curId,
                            String branchId, String splitId, String branch) {
        Commit lca = Commit.getCommit(splitId);
        Commit given = Commit.getCommit(branchId);
        boolean conflict = false;
        for (String file : getUntrackedFileNames()) {
            if (given.containsFile(file)) {
                throw new GittaException("There is an untracked file"
                + "in the way; delete it, or add and commit it first.\n");
            }
        }
        Set<String> files = new HashSet<>(_lastCommit.getObjects().keySet());
        files.addAll(lca.getObjects().keySet());
        files.addAll(given.getObjects().keySet());
        for (String filename : files) {
            boolean givenModified = isModifiedSince(filename, given, lca);
            boolean curModified = isModifiedSince(filename,
                                            _lastCommit, lca);
            boolean givenDeleted = isDeletedSince(filename, given, lca);
            boolean curDeleted = isDeletedSince(filename, _lastCommit, lca);
            boolean givenNew = isNewSince(filename, given, lca);
            boolean curNew = isNewSince(filename, _lastCommit, lca);
            boolean curSame = isSameSince(filename, _lastCommit, lca);
            if (givenModified && curSame) {
                checkoutFileWithID(filename, branchId);
                _stage.addFile(GittaUtils.join(PWD, filename));
            } else if (givenNew && !curNew) {
                checkoutFileWithID(filename, branchId);
                _stage.addFile(GittaUtils.join(PWD, filename));
            } else if (curSame && givenDeleted) {
                rm(GittaUtils.join(PWD, filename));
            } else if ((givenModified && curModified
                        && !branchId.equals(splitId))
                        || (givenModified && curDeleted)
                        || (curModified && givenDeleted)
                        || (givenNew && curNew
                        && differentFile(branchId, curId))) {
                modifyConflictFile(filename, given);
                _stage.addFile(GittaUtils.join(PWD, filename));
                conflict = true;
            }
        }
        _stage.save();
        commit("Merged " + branch + " into " + _head + ".", branchId);
        if (conflict) {
            System.out.print("Encountered a merge conflict.\n");
        }
    }


    /**
     * Validate merge branch is not current head.
     * @param branch branch name
     */
    private static void validateNotHeadBranch(String branch) {
        if (branch.equals(_head)) {
            throw new GittaException("Cannot merge a branch with itself.\n");
        }
    }

    /**
     * If FILE modified in COMMIT after LCA.
     * @param file filename
     * @param commit commit object
     * @param lca lca object
     * @return true if modified
     */
    private static boolean isModifiedSince(String file,
                                Commit commit, Commit lca) {
        return lca.containsFile(file) && commit.containsFile(file)
                && !lca.getObjectId(file).equals(commit.getObjectId(file));
    }

    /**
     * If FILE deleted in COMMIT after LCA.
     * @param file filename
     * @param commit commit object
     * @param lca lca object
     * @return true if deleted
     */
    private static boolean isDeletedSince(String file,
                                    Commit commit, Commit lca) {
        return lca.containsFile(file)
                && !commit.containsFile(file);
    }

    /**
     * If FILE new in COMMIT after LCA.
     * @param file filename
     * @param commit commit object
     * @param lca lca object
     * @return true if new
     */
    private static boolean isNewSince(String file, Commit commit, Commit lca) {
        return !lca.containsFile(file)
                && commit.containsFile(file);
    }

    /**
     * If file same as in lca.
     * @param file filename
     * @param commit commit object
     * @param lca lca object
     * @return true if same
     */
    private static boolean isSameSince(String file, Commit commit, Commit lca) {
        return lca.containsFile(file) && commit.containsFile(file)
                && lca.getObjectId(file).equals(commit.getObjectId(file));
    }

    /**
     * If ID1 file different with ID2 file.
     * @param id1 file blob id 1
     * @param id2 file blob id 2
     * @return true if different
     */
    private static boolean differentFile(String id1, String id2) {
        return id1 != null && id2 != null && id1 != id2;
    }

    /**
     * Modify conflict file named FILENAME in CWD.
     * @param filename filename
     * @param commit commit object
     */
    private static void modifyConflictFile(String filename, Commit commit) {
        GittaObjects curBlob = _lastCommit.getObject(filename);
        GittaObjects otherBlob = commit.getObject(filename);

        String newContent = "<<<<<<< HEAD"
                            + System.getProperty("line.separator", "\n");
        if (curBlob != null) {
            newContent += new String(curBlob.getContent(),
                            StandardCharsets.UTF_8);
        }
        newContent += "=======" + System.getProperty("line.separator", "\n");
        if (otherBlob != null) {
            newContent += new String(otherBlob.getContent(),
                                     StandardCharsets.UTF_8);
        }
        newContent += ">>>>>>>" + System.getProperty("line.separator", "\n");
        GittaIO.writeContents(GittaUtils.join(PWD, filename), newContent);
    }

    /**
     * Find latest common ancestor.
     * @param branch branch name
     * @return lca id
     */
    private static String findLCA(String branch) {
        String cur = _reference.getBranch(_head);
        String other = _reference.getBranch(branch);

        Set<String> visited = new HashSet<>();
        LinkedList<String> curList = new LinkedList<>();
        LinkedList<String> otherList = new LinkedList<>();
        curList.add(cur);
        otherList.add(other);

        while (!curList.isEmpty() || !otherList.isEmpty()) {
            int cnt = curList.size();
            while (cnt != 0) {
                String a = lcaCommit(curList, visited);
                if (a != null) {
                    return a;
                }
                cnt -= 1;
            }
            cnt = otherList.size();
            while (cnt != 0) {
                String b = lcaCommit(otherList, visited);
                if (b != null) {
                    return b;
                }
                cnt -= 1;
            }
        }
        return null;
    }

    /**
     * If head of LIST is lca, then return LIST's head, otherwise return null.
     * @param list queue list
     * @param visited visited set
     * @return lca id if found, null otherwise
     */
    private static String lcaCommit(LinkedList<String> list,
                                    Set<String> visited) {
        String s = list.removeFirst();
        Commit commit = Commit.getCommit(s);
        if (visited.contains(s)) {
            return s;
        }
        if (commit.getParentId() != null) {
            String id = commit.getParentId();
            if (visited.contains(id)) {
                return id;
            }
            list.add(id);
        }
        if (commit.getSecondParentId() != null) {
            String id = commit.getSecondParentId();
            if (visited.contains(id)) {
                return id;
            }
            list.add(id);
        }
        visited.add(s);
        return null;
    }

}
