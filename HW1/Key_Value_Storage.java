package HW1;

import java.io.*;
import java.security.MessageDigest;

public class Key_Value_Storage {
	/* Key作为文件名，文件内容作为Value
	 * 支持给定Value，向存储中添加对应的Key-Value
	 * 给定Key查找对应的Value
	 */
	
	public static String StoragePath;	//工作区目录
	public static String fileStoragePath;	//存储目录，即工作区目录下/mygit/objects
	
	
	public Key_Value_Storage() {	
	}
	
	//带参构造方法，实例化时指定工作区目录
	public Key_Value_Storage(String StoragePath) {
		this.StoragePath = StoragePath;
	}
	
	
	//根据输入的哈希值读对应文件名的文件内容
	public static String cat_file(String hashCode){
		File file = new File(Key_Value_Storage.fileStoragePath + File.separator + hashCode);
		BufferedReader reader = null;
		StringBuffer sbf = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempStr;
			while ((tempStr = reader.readLine()) != null) {
				sbf.append(tempStr);
				sbf.append("\n");
			}
			reader.close();
			return sbf.toString();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return sbf.toString();
	}
	
	
	//向仓库中添加工作区目录下的名为filename的文件
	protected String addFile(String filename) {
		String absPath = Key_Value_Storage.StoragePath + File.separator + filename;	//提供文件绝对路径 
		File file = new File(absPath);	//需要被add的文件
		//String parentPath = file.getParent();	//获取该文件上级目录
		
		try {
			FileInputStream is = new FileInputStream(absPath);	//创建输入流
			FileOutputStream os = new FileOutputStream(Key_Value_Storage.fileStoragePath + File.separator + "temp");	//在仓库内创建一个名为temp的输出流，后续重命名为SHA1值
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
			File output = new File(Key_Value_Storage.fileStoragePath + File.separator + "temp");
			File output_new = new File(Key_Value_Storage.fileStoragePath + File.separator + result);
			output.renameTo(output_new);	//将文件重命名为其内容的SHA1值
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	//在构造方法指定的目录下创建mygit目录作为仓库
	public static void init() {
		File storage = new File(Key_Value_Storage.StoragePath + File.separator + "mygit" + File.separator + "objects");
		if (!storage.exists()) {
				storage.mkdirs();
		}
		//将新建的仓库地址存入类属性
		Key_Value_Storage.fileStoragePath = Key_Value_Storage.StoragePath + File.separator + "mygit" + File.separator + "objects";
	}

}


