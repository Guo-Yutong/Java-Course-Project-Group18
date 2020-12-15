package src;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class TestHashUtils {
    public static void main(String[] args){
        String value = "777";
        try{
            HashUtils str_hash = new HashUtils(value);
            System.out.println(value  + " hash:\t" + str_hash.getSHA1());
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        catch(NoSuchAlgorithmException e){
            System.out.println("当前环境无法求出SHA1值");
        }
    
        File file = new File("src/test.txt");
        if(!file.exists())
        System.out.println("there is no such file");
             else{
                try{
                    HashUtils filehash = new HashUtils(file);
                    System.out.println(file.getName() + " hash:\t" + filehash.getSHA1());
                }
                catch(IOException ex){
                  System.out.println("I/O Errors:no such file");  
                }
                catch(NoSuchAlgorithmException e){
                  System.out.println("当前环境无法求出SHA1值");
                }
            }
    
            File fakefile = new File("src/test1.txt");
            if(!fakefile.exists())
                System.out.println(fakefile.toString() + "is not exists");
            else{
                try{
                HashUtils filehash = new HashUtils(file);
                System.out.println(filehash.getSHA1());
            }
            catch(IOException ex){
              System.out.println("I/O Errors:no such file");  
            }
            catch(NoSuchAlgorithmException e){
              System.out.println("当前环境无法求出SHA1值");
            }
        }
    }
}
