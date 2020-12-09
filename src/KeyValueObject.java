package src;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class KeyValueObject {
    protected String type;
    protected String key;
    protected File file;

    protected KeyValueObject(){
    }
    
    protected void generateKey(File file) throws IOException{
        Hash s = new Hash(file);
        this.key = s.getSHA1();
        this.file = file;
    }

    protected void generateKey(String value) throws IOException{
        Hash s = new Hash(value);
        this.key = s.getSHA1();
    }


    protected String getKey(){
        return this.key;
    }

    protected String getType(){
        return this.type;
    }

    //拷贝一份文件，内容相同，名字为其key值
    protected void copyFile() throws IOException{
        try(
            FileInputStream filein = new FileInputStream("this.file");
            BufferedInputStream input = new BufferedInputStream(filein);
            FileOutputStream fileout = new FileOutputStream(this.key);
            BufferedOutputStream output = new BufferedOutputStream(fileout);
        ){
            int r = 0;
            while((r = input.read())!= -1){
                output.write((byte)r);
            }
        }
    }
}
