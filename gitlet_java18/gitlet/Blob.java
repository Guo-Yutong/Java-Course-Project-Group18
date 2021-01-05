package gitlet;

import java.io.File;
import java.io.Serializable;

/** Represents a Blob file that can be serialized.
 * @author Jiarui Li
 */
public class Blob implements Serializable {

    private static final long serialVersionUID = 10L;
    /** Folder that blob live in. */
    static final File BLOBS_FOLDER =
                        Utils.join(Repository.REPO_FOLDER, "blobs");
    /** Blob object header. */
    private String _header;
    /** Blob object content. */
    private byte[] _content;

    /**
     * Init BLOBS_FOLDER.
     * @return true if init succeed
     */
    public static boolean init() {
        assert !BLOBS_FOLDER.exists();
        return BLOBS_FOLDER.mkdir();
    }

    /**
     * Create a blob object with the specified parameters.
     * @param file source of blob's content
     * @p
     */
    public Blob(File file) {
        if (file.exists()) {
            _content = Utils.readContents(file);
            _header = "Blob " + _content.length;
        }
    }

    /**
     * Return sha1 of blob.
     * @return sha1 of blob
     */
    public String getSha1() {
        return Utils.sha1(Utils.serialize(this));
    }

    /**
     * Contruct a Blob object from file.
     * @param file file that blob object lives in
     * @return blob object
     */
    private static Blob getBlob(File file) {
        if (!file.exists()) {
            return null;
        }
        return Utils.readObject(file, Blob.class);
    }

    /**
     * Construct a Blob object from file.
     * @param sha1 sha1 value of the object
     * @return blob object
     */
    public static Blob getBlob(String sha1) {
        return getBlob(Utils.join(BLOBS_FOLDER, sha1));
    }

    /**
     * Save a Blob object into file.
     */
    public void saveBlob() {
        String sha1 = Utils.sha1(Utils.serialize(this));
        Utils.writeObject(Utils.join(BLOBS_FOLDER, sha1), this);
    }

    /**
     * Read a blob object from BLOB and save it to blobs folder.
     * @param blob
     */
    public static void saveBlob(File blob) {
        assert blob != null;
        getBlob(blob).saveBlob();
    }

    /**
     * Return content of blob.
     * @return blob's content
     */
    public byte[] getContent() {
        return _content;
    }

}
