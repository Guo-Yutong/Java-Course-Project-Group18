package src;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class Hash {
    private String ans;

    public Hash(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        this.ans = Sha1Checksum(is);
    }

    public Hash(String value)throws IOException{
        this.ans = Sha1Checksum(value);
    }
    //计算文件Hash值
    public static String Sha1Checksum(FileInputStream is)throws IOException{
        byte[] buffer = new byte[1024];
        MessageDigest m = MessageDigest.getInstance("SHA-1");
        int numRead = 0;
        do{
            numRead = is.read(buffer);
            if(numRead > 0){
                m.update(buffer,0,numRead);
            }
        }while(numRead!=1);
        is.close();
        String ans = getSha1(m.digest());
        return ans;
    }

    //计算字符串哈希值
    public static String Sha1Checksum(String value)throws IOException{
        MessageDigest m = MessageDigest.getInstance("SHA-1");
        m.update(value.getBytes());
        String ans = getSha1(m.digest());
        return ans;
    }

    public static String getSha1(byte data[]){
        StringBuffer strBuffer = new StringBuffer();
        for(int i = 0;i < data.length;i++){
            strBuffer.append(Integer.toHexString(0xff & data[i]));
        }
        return strBuffer.toString();
    }
    
    //数据域封装设置get方法
    public String getSHA1(){
        return this.ans;
    }

}

