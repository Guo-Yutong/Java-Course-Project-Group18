package src;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    private String ans; //hash值16进制字符串表示

    public Hash(File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream is = new FileInputStrea(file);
        this.ans = Sha1Checksum(is);//计算文件的hash值
    }

    public Hash(String value) throws IOException, NoSuchAlgorithmException {
        this.ans = Sha1Checksum(value);//Hash（tree-value）的情况
    }

    // 计算文件Hash值
    public static String Sha1Checksum(FileInputStream is) throws IOException, NoSuchAlgorithmException {
        byte[] buffer = new byte[1024];//减少磁盘读写次数
        MessageDigest m = MessageDigest.getInstance("SHA-1");
        int numRead = 0;
        do{
            numRead = is.read(buffer);
            if(numRead > 0){
                m.update(buffer,0,numRead);
            }
        }while(numRead!=1);
        is.close(); //释放，也可以使用try-with-source自动关闭
        String ans = getSha1(m.digest()); //把计算出的byte[]传入getSha1中
        return ans;//返回最终的16进制字符串hash值
    }

    //计算字符串的哈希值
    public static String Sha1Checksum(String value)throws IOException, NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("SHA-1");//创建实例
        m.update(value.getBytes());//通过字符串更新实例
        String ans = getSha1(m.digest());//digest出byte[]结果传入getSha1中
        return ans;//得到最终的16进制字符串hash值
    }

    public static String getSha1(byte data[]){ 
        StringBuffer strBuffer = new StringBuffer();
        for(int i = 0;i < data.length;i++){
            strBuffer.append(Integer.toHexString(0xff & data[i]));
        }//避免负数，未考虑16进制第一位为0的情况
        return strBuffer.toString();
    }
    
    //数据域封装
    public String getSHA1(){
        return this.ans;
    }

}

