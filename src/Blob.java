package src;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Blob extends KeyValueObject {

    public Blob(File file) throws IOException, NoSuchAlgorithmException {
        this.type = "blob";//设置type
        generateKey(file);//构造时生成key
    }


    @Override
    public String toString(){ 
        return "100644 blob" + key;
    }

}
