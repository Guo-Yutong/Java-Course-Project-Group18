package HW1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Scanner;

public class Key_Value_Storage {
	/* Key作为文件名，文件内容作为Value
	 * 支持给定Value，向存储中添加对应的Key-Value
	 * 给定Key查找对应的Value
	 */
	private static FileInputStream readFile(String filepath) {	//读文件流
		try {
			File file = new File(filepath);
			FileInputStream is = new FileInputStream(file);
			return is;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	private static byte[] SHA1Checksum(InputStream is) throws Exception{	//SHA1算法，已嵌入add()方法中
		byte[] buffer = new byte[1024];	//缓存器
		MessageDigest complete = MessageDigest.getInstance("SHA-1");
		int numRead = 0;	//当前读到的字节数
		do {
			numRead = is.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		is.close();
		return complete.digest();
	}
	*/
	
	public static void add() {
		File dir = new File("/home/yutong_guo/mygit");
		if (!dir.exists()) {	//创建目录
			dir.mkdir();
		}
		System.out.print("请输入文件路径: ");
		Scanner input = new Scanner(System.in);
		String filepath = input.nextLine();
		FileInputStream is = readFile(filepath);	//创建输入流
		
		try {
			FileOutputStream os = new FileOutputStream(dir+"/temp");	//创建输出流
			byte[] buffer = new byte[1];	//缓存器，只能一个一个字节读，否则填不满会使用末尾字节补齐，改变源文件
											//但一个一个字节读会造成读取速度变慢，待解决
			MessageDigest complete = MessageDigest.getInstance("SHA-1");
			int numRead = 0;	//当前读到的字节数
			while (true) {
				numRead = is.read(buffer);	//边读
				if(numRead == -1) {	//如果写操作紧跟读操作，在while循环最后读到-1时会向文件中写入一个本不存在的空行
					break;
				}
				os.write(buffer);	        //边写
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			}
			is.close();
			os.close();
			byte[] sha1 = complete.digest();	//计算SHA1
			
			String result = "";
			for(int i = 0; i < sha1.length; i++) {
				result += Integer.toString(sha1[i]&0xFF,16);
			}
			//System.out.println(result);
			File output = new File(dir+"/temp");
			File output_new = new File(dir+"/"+result);
			output.renameTo(output_new);	//将文件重命名为其内容的SHA1值
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String arg[]) throws FileNotFoundException {	//测试，可以产生在/mygit目录内以SHA1为文件名、文件内容不变的文件
		add();
		System.out.println("程序结束");
	}

}


