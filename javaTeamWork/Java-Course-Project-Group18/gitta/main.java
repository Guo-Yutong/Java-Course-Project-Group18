public class Main {

    /** 目前工作目录*/
    static final File CWD = new File(".");

    /** 用法: java gitta.Main 字符串数组, 字符串数组由以下成份组成
     *  <命令> <操作数> .... */
    public static void main(String... args) {
        if (args.length == 0) {  //如果命令行为空，提示以下
            System.out.print("Please enter a command.");
            System.exit(0);
        }

        try { //我们传入的字符串数组第一个字符串对应哪种命令，就调用main里面的相应函数
            switch (args[0]) { //字符串数组里的第一个字符串
            case "init":
                init(args); //传入字符串数组，调用main里的init方法，即41行
                break;
            case "add":
                add(args); //同上，调用main里的add方法
                break;
      ……//省略
            case "rm-branch":
                rmBranch(args);
                break;
            case "reset":
                reset(args);
                break;
            case "merge":
                merge(args);
                break;
            default: //第一个字符串不是以上的命令下，输出以下内容
                System.out.print("No command with that name exists.");
            }
        } catch (GitletException e) {
            System.out.print(e.getMsg());//获取异常的详细消息字符串
        }
        System.exit(0);
    }
  
    private static void init(String[] args) {//被17行 init(args)调用
        operandsCheck(args, 1, 0);//调用main里的operandsCheck方法
        validateRepoExist(false);//调用main里的validateRepoExist方法85行
        Repository.init();//调用Repository类的静态方法init()
    }

    private static void add(String[] args) {//同上
        operandsCheck(args, 1, 1);//方法具体是什么，参数为什么这样给，方法那里写
        validateRepoExist(true);
        Repository.add(validateFileExist(args, 1));
    }
……//省略
    private static void checkout(String[] args) {//checkout命令行的三种用法
        if (args.length == 3 && args[1].equals("--")) {//但这个我们不要了
            validateRepoExist(true);
            Repository.checkoutFile(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {//这个也不要了
            validateRepoExist(true);
            Repository.checkoutFileWithID(args[3], args[1]);
        } else {
            operandsCheck(args, 2, 0);//保留这个，就是切换到新分支上
            validateRepoExist(true);
            Repository.checkoutBranch(args[1]);
        }
    }
    /**operandCheck方法
     * 核对输入进来的操作数
     * @参数：输入进来的字符串数组
     * @参数：期望的操作数
     * @参数： type =0 等于, >0 多于, <0 少于
     *比如：对于切换新分支 &checkout newbranch 里是operandsCheck(args, 2, 0);
     *意思是：expected=2（我期望输入的字符串数组是checkout newbranch，即length为2）
     *type=0是我要求你必须是length=2
     */
    private static void operandsCheck(String[] args, int expected, int type) {
        if ((type > 0 && args.length <= expected)//要求正确输入是多于期望，但却<=
            || (type == 0 && args.length != expected)//正确输入是等于期望，但却！=
            || (type < 0 && args.length >= expected)) {//正确输入是小于期望，但却>=
            System.out.print("Incorrect operands.");//有以上情况输出此内容
            System.exit(0);
        }
    }
  //下面是几个Validate方法
    /**ValidateReopoExist方法
     * 检查仓库是否存在
     * @参数 验证存在为true, 否则为false
     */
    private static void validateRepoExist(boolean expected) {
        if (Repository.isExisted() != expected) {//如果repository存在性和期待的不一样
            if (expected) {//期待仓库Repository是有的，却没有
             //即你还没init初始化就命令行输入别的操作了 
                System.out.print("Not in an initialized Gitlet directory");//则输出
                System.exit(0);
            } else {//期待仓库是空的，却不是空的
              //即你已经init过了，有repository了却又init时输出下列句子
                System.out.print("A Gitlet version-control system"
                    + "already exists in the current directory.");
                System.exit(0);
            }
        }
    }
    /** 检查验证暂存区是否为空*/
    private static void validateStageEmpty() {
        if (Stage.getStage().isEmpty()) {//暂存区为空
          //没有可以commit的暂存区内容
            System.out.print("No changes added to the commit.");
            System.exit(0);
        }
    }
    /**
     * 检查验证commit的附加消息是否为空
     */
    private static void validateMSG(String msg) {
        assert msg != null;
        if (msg.trim().equals("")) {//commit的时候没有输入备注消息，则输出以下
            System.out.print("Please enter a commit message");
            System.exit(0);
        }
    }
    /**
     * 验证检查文件是否存在，只被add方法调用
     * @参数：输入的字符串数组
     * @参数：偏移量，add的时候次参数为1，意思是我们输入$java gitta.Main add a.txt b.txt的时候，不管第一个字符“add”，从“a.txt”开始创建文件对象组成列表调用Repository的add方法
     * @返回现存的List<File>
     */
    private static List<File> validateFileExist(String[] args, int offset) {
        ArrayList<File> files = new ArrayList<>();//建立数组列表
        for (int i = offset; i < args.length; i += 1) {
            File f = Utils.join(CWD, args[i]);//新建File对象：当前工作目录下的这个文件
            if (!f.exists()) {//如果这个文件不存子啊，输出如下
                System.out.print("File does not exist.");
                System.exit(0);
            }
            files.add(f);//往数组列表中添加这个f文件对象
        }
        return files;//遍历完返回这个文件组成的列表
    }

}