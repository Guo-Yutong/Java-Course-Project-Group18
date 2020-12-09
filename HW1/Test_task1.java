package HW1;

import java.io.FileNotFoundException;

public class Test_task1 {
	//测试用例
	public static void main(String arg[]) throws FileNotFoundException {
		Key_Value_Storage kvs = new Key_Value_Storage("/home/yutong_guo/Software_Eng");	//构造函数指定工作区目录
		Blob file = new Blob("class3.txt");
		kvs.init();	//初始化仓库
		
		file.add();	//向仓库内添加文件，直接输入仓库目录下的相对路径即可
		
		System.out.println(file);	//打印blob类型及哈希值
		System.out.print(Key_Value_Storage.cat_file("f380708af1c283337519fae716a470201aeaafa2"));	//根据文件名打印文件内容，文件不存在抛出异常
		System.out.println("\n" + "=======end=======");
	}

}
