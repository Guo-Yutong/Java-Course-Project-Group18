package src;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class Tree extends KeyValueObject {
    private String value = ""; //tree的value值


    public Tree(File file) throws Exception {
        this.type = "tree"; //设置type
        for(File f:file.listFiles()){ //for each遍历
            if(f.isFile()){ //100644 blob hashkey a.txt 格式
                value = value + "\n" + "100644 blob " + new Blob(f).getKey() + " " + f.getName();
            }
            else if(f.isDirectory()){
                value = value + "\n" + "040000 tree " + new Tree(f).getKey() + " " + f.getName();
            }
        }
        generateKey(value); //遍历生成完了tree的value后计算hash得到key
    }

    //数据域封装设置get方法
    public String getValue(){
        return this.value;
    }

    //040000 tree hashkey
    @Override
    public String toString(){
        return "040000 tree" + key;
    }

    //重写父类的复制文件方法，文件名字为key值，文件内容为value值
    @Override
    public void writeFile() throws IOException{
        PrintWriter cp = new PrintWriter(this.key);//PrintWriter向文本输出
        cp.print(value);
        cp.close();//
    }

}

