package gitta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Stage implements GittaIO {
	private static final long serialVersionUID = 10L;
    /** Folder that staging files live in. */
    static final File STAGE_FOLDER =
                        GittaUtils.join(Repository.REPO_FOLDER, "stage");
    /** Path that index object live in. */
    static final File INDEX_PATH = GittaUtils.join(Repository.REPO_FOLDER, "index");
    /** Mapping from removed filename to its blob id. */
    private HashSet<String> _removedFiles;
    /** Mapping from staged filename to its blob id. */
    private HashMap<String, String> _stagedFiles;

    /** Construct method of stage. */
    private Stage() {
        _removedFiles = new HashSet<>();
        _stagedFiles = new HashMap<>();
    }

    /**
     * Init staging area.
     * @return if successfully init
     */
    public static boolean init() {
        return STAGE_FOLDER.mkdir();
    }

    /** Return current stage. */
    public static Stage getStage() {
        Stage stage = fromFile();
        if (stage == null) {
            stage = new Stage();
        }
        return stage;
    }

    /** Return if current stage is empty. */
    public boolean isEmpty() {
        return _removedFiles.isEmpty() && _stagedFiles.isEmpty();
    }

    /**
     * Return set of removed files.
     * @return removed file set
     */
    public HashSet<String> getRemovedFiles() {
        return _removedFiles;
    }

    /**
     * Return mapping of staged file mappings.
     * @return staged file mappings
     */
    public HashMap<String, String> getStagedFiles() {
        return _stagedFiles;
    }

    /**
     * Return list of staged file names.
     * @return list of filenames
     */
    public List<String> getStagedFileNames() {
        return new ArrayList<>(_stagedFiles.keySet());
    }

    /**
     * Return list of removed file names.
     * @return list of filenames
     */
    public List<String> getRemovedFileNames() {
        return new ArrayList<>(_removedFiles);
    }

    /**
     * If file exists in staging area.
     * @param file file
     * @return if FILE staged
     */
    public boolean isFileStaged(File file) {
        return isFileStaged(file.getName());
    }

    /**
     * If file exists in staging area.
     * @param name filename
     * @return if file with NAME staged
     */
    public boolean isFileStaged(String name) {
        assert name != null;
        return _stagedFiles.containsKey(name);
    }

    /**
     * If file staged for remove.
     * @param file removed file
     * @return if successfully removed
     */
    public boolean isFileRemoved(File file) {
        return isFileRemoved(file.getName());
    }

    /**
     * If file staged for remove.
     * @param name filename
     * @return if successfully removed
     */
    public boolean isFileRemoved(String name) {
        assert name != null;
        return _removedFiles.contains(name);
    }

    /**
     * If file with blob exists in staging area.
     * @param name filename
     * @param sha1 stage file id
     * @return true if blobl exists in staging area
     */
    public boolean isBlobStaged(String name, String sha1) {
        return isFileStaged(name) && _stagedFiles.get(name).equals(sha1);
    }

    /**
     * Remove file from stage area, assuming file exists.
     * @param file Unstaged file
     */
    public void unstagedFile(File file) {
        assert file.exists();
        assert _stagedFiles.containsKey(file.getName());
        String sha1 = _stagedFiles.remove(file.getName());
        if (!_stagedFiles.containsValue(sha1)) {
            GittaUtils.join(STAGE_FOLDER, sha1).delete();
        }
        save();
    }

    /**
     * Remove FILE from staging area's REMOVEDFILES mapping.
     * @param file unremoved file
     */
    public void unremovedFile(File file) {
        assert _removedFiles.contains(file.getName());
        _removedFiles.remove(file.getName());
        save();
    }

    /**
     * Construct stage object from INDEX_PATH.
     * @return stage object
     */
    private static Stage fromFile() {
        if (!STAGE_FOLDER.exists() || !INDEX_PATH.exists()) {
            return null;
        }
        return GittaIO.readObject(INDEX_PATH, Stage.class);
    }

    /** Save stage object into INDEX_PATH. */
    public void save() {
        GittaIO.writeObject(INDEX_PATH, this);
    }

    /**
     * Add file into staging area, assuming file exists.
     * @param file Added file
     */
    public void addFile(File file) {
        assert file.exists();

        String filename = file.getName();
        if(file.isFile()) {
        	Blob blob = new Blob(file);
	        String sha1 = blob.getSHA1();
	        if (_stagedFiles.containsKey(filename)) {
	            if (_stagedFiles.get(filename).equals(sha1)) {
	                return;
	            }
	            String oldsha1 = _stagedFiles.put(filename, sha1);
	            if (!_stagedFiles.containsValue(oldsha1)) {
	                deleteBlob(oldsha1);
	            }
	        } else {
	            _stagedFiles.put(file.getName(), sha1);
	            saveBlob(sha1, blob);
	        }
	        save();
        }else if(file.isDirectory()) {
        	Tree tree = new Tree(file);
        	String sha1 = tree.getSHA1();
        	if (_stagedFiles.containsKey(filename)) {
	            if (_stagedFiles.get(filename).equals(sha1)) {
	                return;
	            }
	            String oldsha1 = _stagedFiles.put(filename, sha1);
	            if (!_stagedFiles.containsValue(oldsha1)) {
	                deleteBlob(oldsha1);
	            }
	        } else {
	            _stagedFiles.put(file.getName(), sha1);
	            saveTree(sha1, tree);
	        }
        }
    }

    /**
     * Add file into removed staging area.
     * @param file file removed
     */
    public void addRemovedFile(File file) {
        assert file != null;
        _removedFiles.add(file.getName());
        save();
    }

    /**
     * Return a staged file with FILENAME.
     * @param filename filename
     * @return file staged in staging area
     */
    public File getBlob(String filename) {
        assert _stagedFiles.containsKey(filename);
        return GittaUtils.join(STAGE_FOLDER, _stagedFiles.get(filename));
    }

    /**
     * Clear stage extents.
     */
    public void clear() {
        for (String sha1 : _stagedFiles.values()) {
            if (GittaUtils.join(STAGE_FOLDER, sha1).exists()) {
                deleteBlob(sha1);
            }
        }
        _stagedFiles.clear();
        _removedFiles.clear();
        save();
    }

    /**
     * Save a blob to staging area.
     * @param sha1 id of blob file
     * @param blob blob object
     */
    private void saveBlob(String sha1, Blob blob) {
        GittaIO.writeObject(GittaUtils.join(STAGE_FOLDER, sha1), blob);
    }
    
    /**
     * Save a tree to staging area.
     * @param sha1 id of tree file
     * @param tree tree object
     */
    private void saveTree(String sha1, Tree tree) {
        GittaIO.writeObject(GittaUtils.join(STAGE_FOLDER, sha1), tree);
    }

    /**
     * Delete a blob from staging area.
     * @param sha1 id of blob file
     */
    private void deleteBlob(String sha1) {
        assert GittaUtils.join(STAGE_FOLDER, sha1).delete();
    }

}
