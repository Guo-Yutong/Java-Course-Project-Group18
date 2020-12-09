import java.io.File;
import src.Tree;
import src.Blob;

public class UnitTest {
    public static void main(String[] args){
        testName("README.md");
        testName("src");
    }

    public static void testName(String filename){
        File file = new File(filename);
        try{
            if(file.isDirectory()){
                Tree tree = new Tree(file);
                System.out.println(tree);
            }
            else{
                Blob blob = new Blob(file);
                System.out.println(blob);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
