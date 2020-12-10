package src;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class KeyValueObject {
    protected String type;//类型
    protected String key; //key
    protected File file; 

    protected KeyValueObject() {
    }

    //生成key的函数，参数为文件
    protected void generateKey(File file) throws IOException, NoSuchAlgorithmException {
        Hash s = new Hash(file); //创建hash类
        this.key = s.getSHA1(); //使用hash类方法，可以得到16进制字符串key值
        this.file = file; 
    }

    //生成key的函数，参数为hash值字符串（tree的value）
    protected void generateKey(String value) throws IOException, NoSuchAlgorithmException {
        Hash s = new Hash(value);
        this.key = s.getSHA1();
    }


    public String getKey(){ //数据域封装
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
            }//为了写文件安全，写时未设置buffer
        }
    }
}
