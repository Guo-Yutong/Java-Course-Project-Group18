package src;
import java.io.File;
import java.io.IOException;

public class Blob extends KeyValueObject{

    public Blob(File file) throws IOException{
        this.type = "blob";
        generateKey(file);
    }


    @Override
    public String toString(){
        return "100644 blob" + key;
    }

}
