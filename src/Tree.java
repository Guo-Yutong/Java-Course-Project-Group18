package src;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class Tree extends KeyValueObject {
    private String value = "";
    public Tree(String path) throws IOException{
        File file = new File(path);
        this.type = "tree";
    }

    public Tree(File file) throws Exception {
        for(File f:file.listFiles()){
            if(f.isFile()){
                value = value + "\n" + "100644 blob " + new Blob(f).getKey() + " " + f.getName();
            }
            else if(f.isDirectory()){
                value = value + "\n" + "100644 tree " + new Tree(f).getKey() + " " + f.getName();
            }
        }
        generateKey(value);
    }


    @Override
    public String toString(){
        return "040000 tree" + key;
    }

    @Override
    public void copyFile() throws IOException{
        PrintWriter cp = new PrintWriter(this.key);
        cp.print(value);
        cp.close();
    }

}

