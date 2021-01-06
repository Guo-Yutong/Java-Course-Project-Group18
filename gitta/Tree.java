package gitta;

import java.io.File;
import java.nio.file.Paths;

public class Tree extends GittaObjects implements GittaIO{
	
	private static final long serialVersionUID = 10L;

    public String _actContent = "";



    /**
     * Create a blob object with the specified parameters.
     * @param file source of blob's content
     * @p
     */
    public Tree(File file) {
		_type = "tree";
		for(File f:file.listFiles()){ //for each遍历
            if(f.isFile()){ //100644 blob hashkey a.txt 格式
            	if (_actContent.length() == 0) {
            		_actContent = _actContent + "100644 blob " + new Blob(f).SHA1() + " " + f.getName();
            	}else {
            		_actContent = _actContent + "\n" + "100644 blob " + new Blob(f).SHA1() + " " + f.getName();
            	}
            }
            else if(f.isDirectory()){
            	if (_actContent.length() == 0) {
            		_actContent = _actContent + "040000 tree " + new Tree(f).SHA1() + " " + f.getName();
            	}else {
            		_actContent = _actContent + "\n" + "040000 tree " + new Tree(f).SHA1() + " " + f.getName();
            	}
            }
        }
		_content = GittaIO.serialize(_actContent);
    }

    /**
     * Return sha1 of blob.
     * @return sha1 of blob
     */
    
    public String SHA1() {
    	return super.getSHA1(GittaIO.serialize(this._actContent));
    }

    /**
     * Contruct a Blob object from file.
     * @param file file that blob object lives in
     * @return blob object
     */
    private static Tree getTree(File file) {
        if (!file.exists()) {
            return null;
        }
        return GittaIO.readObject(file, Tree.class);
    }
    
    public static Tree getTree(String sha1) {
        return getTree(GittaUtils.join(OBJECTS_FOLDER, sha1));
    }
    
    /**
     * Save a Blob object into file.
     */
    public void saveTree() {
        String sha1 = super.getSHA1(GittaIO.serialize(this));
        GittaIO.writeObject(GittaUtils.join(OBJECTS_FOLDER, sha1), this);
    }

    /**
     * Read a blob object from BLOB and save it to blobs folder.
     * @param blob
     */
    public static void saveTree(File tree) {
        assert tree != null;
        getTree(tree).saveTree();
    }

}
