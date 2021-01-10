package src;

import java.io.*;
import java.security.NoSuchAlgorithmException;

public abstract class KeyValueObject {
    protected String type;
    protected String key; 
    protected String value;
    protected File file; 

    KeyValueObject() {
    }

    //生成key的函数，参数为文件
    protected void generateKey(File file) throws IOException, NoSuchAlgorithmException {
        HashUtils s = new HashUtils(file); 
        this.key = s.getSHA1(); //使用hash类方法，可以得到16进制字符串key值
        this.file = file; 
    }

    //生成key的函数，参数为字符串
    protected void generateKey(String value) throws IOException, NoSuchAlgorithmException {
        HashUtils s = new HashUtils(value);
        this.key = s.getSHA1();
    }


    public String getKey(){ //数据域封装
        return this.key;
    }

    protected String getType(){
        return this.type;
    }

    protected String getValue(){
        return this.value;
    }

    //复制一份文件，内容相同，名字为其key值
    protected void writeFile() throws IOException{
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
