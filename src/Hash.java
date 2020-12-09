package src;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class Hash {
    private String ans;

    public Hash(String value) throws IOException {
        this.ans = SHA1Checksum(value);
    }

    
    //计算一个具体的hash值，通过getSha1方法返回字符串
    public static String SHA1Checksum(String value) throws IOException {
        File file = new File(value);
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        
        //计算一个字符串的hash值
        if(!file.exists()){
            complete.update(value.getBytes());   
        }

        //计算一个文件的hash值
        if(file.isFile()){
            FileInputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead = 0;
            do {
                numRead = is.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            is.close();
        }

        //计算一个文件夹的hash值
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(int i = 0;i < files.length;i++){
                if(files[i].isFile()){
                complete.update(files[i].getName().getBytes());
                FileInputStream isfl = new FileInputStream(files[i]);
                String flsha = SHA1Checksum(isfl);
                complete.update(flsha.getBytes());
                isfl.close();  
            }
                if(files[i].isDirectory()){
                    complete.update(files[i].getName().getBytes());
                    File subfile = new File(value + File.separator + files[i].getName());
                    SHA1Checksum(subfile);
                }
            }
        }
        //以上所有update完后计算最后hash值
        getSha1(complete.digest());
    }


    public static String getSha1(byte data[]){
        StringBuffer strBuffer = new StringBuffer();
        for(int i = 0;i < data.length;i++){
            strBuffer.append(Integer.toHexString(0xff & data[i]));
        }
        return strBuffer.toString();
    }

    public String getSHA1(){
        return this.ans;
    }

}

