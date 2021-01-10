package gitta;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class Tree extends GittaObjects implements GittaIO{
	
	private static final long serialVersionUID = 10L;
	
//	private static File PWD = new File(".");
	/** The actual content of the tree */
    private String _actContent = "";
//    /** A container of blobs under the tree */
//    private HashMap<String,String> blobs;
//    /** A container of trees under the tree */
//    private HashMap<String,String> trees;



    /**
     * Create a Tree object with the specified parameters.
     * @param file source of tree's content
     */
    public Tree(File file) {
		_type = "tree";
		for(File f:file.listFiles()){ //for each遍历
            if(f.isFile()){ //100644 blob hashkey a.txt 格式
            	Blob subBlob = new Blob(f);
//            	String blobSHA1 = subBlob.getSHA1();
            	if (_actContent.length() == 0) {
            		_actContent = _actContent + "100644 blob " + subBlob.getSHA1() + " " + f.getName();
            	}else {
            		_actContent = _actContent + "\n" + "100644 blob " + subBlob.getSHA1() + " " + f.getName();
            	}
//            	blobs.add(subBlob);
            }
            else if(f.isDirectory()){
            	Tree subTree = new Tree(f);
            	if (_actContent.length() == 0) {
            		_actContent = _actContent + "040000 tree " + subTree.getSHA1() + " " + f.getName();
            	}else {
            		_actContent = _actContent + "\n" + "040000 tree " + subTree.getSHA1() + " " + f.getName();
            	}
//            	trees.add(subTree);
            }
        }
		_content = GittaIO.serialize(_actContent);
    }

    /**
     * Return sha1 of tree.
     * @return sha1 of tree
     */
    
    public String getSHA1() {
    	return super.getSHA1(GittaIO.serialize(this._actContent));
    }

    /**
     * Contruct a Tree object from file.
     * @param file file that tree object lives in
     * @return tree object
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
     * Save a Tree object into file.
     */
    public void saveTree() {
        String sha1 = super.getSHA1(GittaIO.serialize(this));
        GittaIO.writeObject(GittaUtils.join(OBJECTS_FOLDER, sha1), this);
    }

    /**
     * Read a Tree object from tree and save it to blobs folder.
     * @param tree
     */
    public static void saveTree(File tree) {
        assert tree != null;
        getTree(tree).saveTree();
    }

}
