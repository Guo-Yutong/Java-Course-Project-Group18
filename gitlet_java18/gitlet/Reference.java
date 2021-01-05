package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class represents on-disk branches content.
 * @author Jiarui Li
 */
public class Reference implements Serializable {

    /** Serial id. */
    private static final long serialVersionUID = 10L;
    /** Folder that refs live in. */
    private static final File REF_PATH =
                        Utils.join(Repository.REPO_FOLDER, "refs");
    /** Mapping from branch name to branch ID. */
    private HashMap<String, String> _branches;

    /** Reference construct method. */
    private Reference() {
        _branches = new HashMap<>();
    }

    /**
     * Get current repo's references from file.
     * @return Reference object
     */
    public static Reference getReference() {
        if (!REF_PATH.exists()) {
            return new Reference();
        }
        Reference reference = Utils.readObject(REF_PATH, Reference.class);
        if (reference == null) {
            reference = new Reference();
        }
        return reference;
    }

    /**
     * Return SHA-1 value of BRANCHNAME if exists.
     *
     * @param branchName branchname
     * @return sha-1 value of branch commit
     */
    public String getBranch(String branchName) {
        if (!_branches.containsKey(branchName)) {
            return null;
        }
        return _branches.get(branchName);
    }

    /**
     * Init reference file and create master reference.
     */
    public static void init() {
        Reference reference = getReference();
        reference.addBranch("master", Commit.init());
        reference.saveFile();
    }

    /**
     * update existed branch.
     *
     * @param name branch name
     * @param id new commit id
     */
    public void branchUpdate(String name, String id) {
        assert _branches.containsKey(name);
        _branches.put(name, id);
        saveFile();
    }

    /**
     * Add new branch.
     * @param name branch name
     * @param id commit id
     */
    public void addBranch(String name, String id) {
        assert !_branches.containsKey(name);
        _branches.put(name, id);
        saveFile();
    }

    /**
     * remove a branch.
     * @param branch branch name
     */
    public void removeBranch(String branch) {
        assert _branches.containsKey(branch);
        _branches.remove(branch);
        saveFile();
    }

    /** Save index file. */
    private void saveFile() {
        Utils.writeObject(REF_PATH, this);
    }

    /**
     * Return branch names list.
     * @return branch name list
     */
    public List<String> getBranchNames() {
        return new ArrayList<>(_branches.keySet());
    }

    /**
     * Return if repository contains BRANCH.
     * @param branch branch name
     * @return true if contains
     */
    public boolean containsBranch(String branch) {
        return _branches.containsKey(branch);
    }

}
