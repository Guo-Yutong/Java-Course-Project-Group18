package gitta;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Commit extends GittaObjects implements GittaIO{

	private static final long serialVersionUID = 10L;

    /** Folder that Commit objects live in. */

    /** Commit file header. */
    private String _header;
    /** Parent commit id. */
    private String _parentId;
    /** Second parent commit id. */
    private String _secondParentId;
    /** commit message. */
    private String _msg;
    /** Mapping from blob or tree filename to its Id. */
    private HashMap<String, String> _objects;
    /** Commit timestamp. */
    private String _timestamp;
    /** Id shorthand len. */
    private static final int ID_SHORTHAND_LEN = 40;

    /**
     * Commit construct method.
     * @param msg commit message
     * @param timestamp commit timestamp if current
     * @param parentId commit parentid
     * @param secondParentId commit secondparentid
     * @param blobs commit blobs
     */
    Commit(String msg, boolean timestamp, String parentId,
           String secondParentId, HashMap<String, String> objects) {
        _msg = msg;
        _timestamp = createTimestamp(timestamp);
        _parentId = parentId;
        _secondParentId = secondParentId;
        if (objects == null) {
            _objects = new HashMap<>();
        }
    }

    /**
     * Commit copy construct method.
     * @param other other commit
     * @param parentId commit parentid
     * @param msg commit message
     */
    Commit(Commit other, String parentId, String msg) {
        _msg = msg;
        _timestamp = createTimestamp(true);
        _parentId = parentId;
        _secondParentId = null;
        _objects = other._objects;
    }

    /**
     * Commit copy construct method.
     * @param other other commit
     * @param parentId commit parentid
     * @param secondParentId commit secondparent id
     * @param msg commit msg
     */
    Commit(Commit other, String parentId, String secondParentId, String msg) {
        _msg = msg;
        _timestamp = createTimestamp(true);
        _parentId = parentId;
        _secondParentId = secondParentId;
        _objects = other._objects;
    }

    /**
     * Init commit management.
     * @return init commit id
     * @throws GitletException
     */
    public static String init() throws GittaException {
        assert !OBJECTS_FOLDER.exists();
        OBJECTS_FOLDER.mkdir();
        Commit commit = new Commit("initial commit", false, null,
                     null, null);
//        Blob.init();
        return commit.saveFile();
    }

    /**
     * If sha1 exists in _BLOBS.
     * @param sha1 id of blob
     * @return true if current commit contains blob
     */
    public boolean containsObject(String sha1) {
        return _objects.containsValue(sha1);
    }

    /**
     * If file exists in commit.
     * @param file file
     * @return true if current contains FILE
     */
    public boolean containsFile(File file) {
        return containsFile(file.getName());
    }

    /**
     * If file exists in commit.
     * @param name filename
     * @return true if current commit contains NAME
     */
    public boolean containsFile(String name) {
        return _objects.containsKey(name);
    }

    /**
     * Return blob file id in commit.
     * @param file blob file in current commit
     * @return blob file id
     */
    public String getFileId(File file) {
        return _objects.get(file.getName());
    }

    /**
     *  Create timestamp according to current time.
     * @param current if current time
     * @return timestamp
     */
    private String createTimestamp(boolean current) {
        Date date = new Date();
        if (!current) {
            date = new Date(0);
        }
        SimpleDateFormat fmt =
                        new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z",
                                             Locale.CHINA);
        return "Date: " + fmt.format(date);
    }

    /**
     * Commit a blob file.
     * @param filename commited file name
     * @param blob commit file's blob
     */
    public void commitFile(String filename, File object) {
    	GittaObjects obj = GittaIO.readObject(object);
    	if (obj instanceof Blob) {
	        _objects.put(filename, object.getName());
	        Blob.saveBlob(object);
    	}else if(obj instanceof Tree) {
    		_objects.put(filename, object.getName());
    		Tree.saveTree(object);
    	}
    }

    /** Remove blob filename from mapping.
     * @param filename remove file name
     */
    public void removeFile(String filename) {
        _objects.remove(filename);
    }

    /**
     * Return commit object according to commit id.
     * @param id commit id
     * @return commit object
     */
    public static Commit getCommit(String id) {
        if (id == null) {
            return null;
        }
        if (id.length() < ID_SHORTHAND_LEN) {
            for (String name
                        : GittaUtils.plainFilenamesIn(OBJECTS_FOLDER)) {
                if (name.indexOf(id) == 0) {
                    return GittaIO.readObject(GittaUtils.join(OBJECTS_FOLDER, name),
                                            Commit.class);
                }
            }
        } else if (id.length() == ID_SHORTHAND_LEN
                   && GittaUtils.join(OBJECTS_FOLDER, id).exists()) {
            return GittaIO.readObject(GittaUtils.join(OBJECTS_FOLDER, id),
                                    Commit.class);
        }
        return null;
    }

    /** Save commit object into file. Return commit object's id. */
    public String saveFile() {
        byte[] content = GittaIO.serialize(this);
        String id = super.getSHA1(content);
        GittaIO.writeObject(GittaUtils.join(OBJECTS_FOLDER, id), this);
        return id;
    }

    /** Return parent commit's id. */
    public String getParentId() {
        return _parentId;
    }

    /** Return second parent commit's id. */
    public String getSecondParentId() {
        return _secondParentId;
    }

    /**
     * Return Object id according to blob file name.
     * @param filename filename in current commit
     * @return object id
     */
    public String getObjectId(String filename) {
        if (!_objects.containsKey(filename)) {
            return null;
        }
        return _objects.get(filename);
    }

    /**
     * Return object id according to file.
     * @param file file
     * @return corresponding objectId of FILE
     */
    public String getObjectId(File file) {
        return getObjectId(file.getName());
    }

    /**
     * Return object according to filename.
     * @param filename
     * @return corresponding object of FILENAME
     */
    public GittaObjects getObject(String filename) {
        if (!_objects.containsKey(filename)) {
            return null;
        }
        GittaObjects obj = GittaIO.readObject(filename);
        if (obj instanceof Blob) {
        	return Blob.getBlob(_objects.get(filename));
        }else if (obj instanceof Tree) {
        	return Tree.getTree(_objects.get(filename));
        }
		return null;
    }

    /** Return current commit's objects set. */
    public Map<String, String> getObjects() {
        return _objects;
    }

    /** Return commit's msg. */
    public String getMSG() {
        return _msg;
    }

    /** Return commit's timestamp. */
    public String getTimestamp() {
        return _timestamp;
    }

    /**
     * Return all commit in commit folder.
     * @return list of commit object
     */
    public static List<Commit> getAllCommits() {
        List<Commit> commits = new ArrayList<>();
        List<String> ids = GittaUtils.plainFilenamesIn(OBJECTS_FOLDER);
        for (String id : ids) {
            commits.add(getCommit(id));
        }
        return commits;
    }

    /** Print log of all commits in commits folder. */
    public static void printAllLog() {
        List<String> ids = GittaUtils.plainFilenamesIn(OBJECTS_FOLDER);
        for (String id : ids) {
            getCommit(id).printLog(id);
            System.out.println();
        }
    }

    /**
     * Print log of commit.
     * @param id commit id
     */
    public void printLog(String id) {
        System.out.println("===");
        System.out.println("commit " + id);
        if (getSecondParentId() != null) {
            System.out.println("Merge: " + getParentId().substring(0, 7) + " "
                                + getSecondParentId().substring(0, 7));
        }
        System.out.println(getTimestamp());
        System.out.println(getMSG());
        System.out.println();
    }

    /**
     * Find commits with the msg.
     * @param msg
     * @return list of commit id with msg equals to MSG
     */
    public static List<String> find(String msg) {
        List<String> ids = GittaUtils.plainFilenamesIn(OBJECTS_FOLDER);
        if (ids == null) {
            return new ArrayList<>();
        }
        List<String> commits = new ArrayList<>();
        for (String id : ids) {
            Commit commit = getCommit(id);
            if (commit._msg.equals(msg)) {
                commits.add(id);
            }
        }
        return commits;
    }

    /**
     * Restore committed file to WD.
     * @param file file
     * @throws GitletException
     */
    public void restoreFile(File file) throws GittaException {
        if (!containsFile(file)) {
            return;
        }
        Blob blob = Blob.getBlob(_objects.get(file.getName()));
        GittaIO.writeContents(file, blob.getContent());
    }

    /**
     * Restore committed file to WD.
     * @param dir WD
     */
    public void restoreCommit(File dir) {
        for (String filename : _objects.keySet()) {
            Blob blob = Blob.getBlob(_objects.get(filename));
            GittaIO.writeContents(GittaUtils.join(dir, filename),
                                blob.getContent());
        }
    }

    /** Return Blob Names list. */
    public List<String> getObjectNames() {
        return new ArrayList<>(_objects.keySet());
    }

}

