package gitta;

import java.io.File;
import java.nio.file.Paths;

public class Blob extends GittaObjects implements GittaIO {
	private static final long serialVersionUID = 10L;


    public Blob(File file) {
        if (file.exists()) {
            _content = GittaIO.readContents(file);
            _type = "blob";
        }
    }
    
    public String SHA1() {
        return super.getSHA1(GittaIO.serialize(this));
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
        return GittaIO.readObject(file, Blob.class);
    }
    
    public static Blob getBlob(String sha1) {
        return getBlob(GittaUtils.join(OBJECTS_FOLDER, sha1));
    }
    
    /**
     * Save a Blob object into file.
     */
    public void saveBlob() {
        String sha1 = super.getSHA1(GittaIO.serialize(this));
        GittaIO.writeObject(GittaUtils.join(OBJECTS_FOLDER, sha1), this);
    }

    /**
     * Read a blob object from BLOB and save it to blobs folder.
     * @param blob
     */
    public static void saveBlob(File blob) {
        assert blob != null;
        getBlob(blob).saveBlob();
    }


}
