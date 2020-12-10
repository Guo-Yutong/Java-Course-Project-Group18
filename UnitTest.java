import src.Blob;
import src.Tree;

import java.io.File;

public class UnitTest {
    public static void testName(String filename){
        File file = new File(filename);
        try{
            if(file.isDirectory()) {
                Tree tree = new Tree(file);
                System.out.println(tree);
                assert tree.getKey().length() == 40;
            }
            else{
                Blob blob = new Blob(file);
                System.out.println(blob);
                assert blob.getKey().length() == 40;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void testContent(String filename){
        File file = new File(filename);
        try{
            if(file.isDirectory()){
                System.out.println(new Tree(file).getValue());
            }
            else{
                System.out.println(new Blob(file));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void testName(File file){
        try{
            if(file.isDirectory()){
                Tree tree = new Tree(file);
                System.out.println(tree);
                assert tree.getKey().length() == 40;
            }
            else{
                Blob blob = new Blob(file);
                System.out.println(blob);
                assert blob.getKey().length() == 40;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void testContent(File file){
        try{
            if(file.isDirectory()){
                System.out.println(new Tree(file).getValue());
            }
            else{
                System.out.println(new Blob(file));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static File genFile(){
        File root = new File("root");
        File blob1 = new File(root,"blob1");
        File tree1 = new File(root,"tree1");
        File blob2 = new File(tree1,"blob2");
        return root;
    }

    public static void main(String[] args){
        File root = new File(".");
        testName(root);
        testContent(root);

    }
}
