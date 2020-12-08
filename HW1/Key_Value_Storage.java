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
	
	/*
	private static FileInputStream readFile(String filepath) {	//读文件流已嵌入add()方法中
		try {
			File file = new File(filepath);
			FileInputStream is = new FileInputStream(file);
			return is;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	*/

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
	
	public static void add(String filepath) {
		File file = new File(filepath);	//需要被add的文件
		String parentPath = file.getParent();	//获取该文件上级目录
		
		//若文件上级目录内没有git仓库则创建之
		File dir = new File(parentPath + File.separator + "mygit");
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		try {
			FileInputStream is = new FileInputStream(filepath);	//创建输入流
			FileOutputStream os = new FileOutputStream(dir+ File.separator + "temp");	//在仓库内创建一个名为temp的输出流，后续重命名为SHA1值
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
			File output = new File(dir + File.separator + "temp");
			File output_new = new File(dir + File.separator + result);
			output.renameTo(output_new);	//将文件重命名为其内容的SHA1值
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String arg[]) throws FileNotFoundException {	//测试，可以产生在/mygit目录内以SHA1为文件名、文件内容不变的文件
		add("/home/yutong_guo/Software_Eng/class2.txt");
		System.out.println("程序结束");
	}

}


