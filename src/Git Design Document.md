# JavaGit设计文档

**@author：java18小组**

参考git的.git文件

```shell
$ mkdir hellogit
$ git init
$ echo '111' > test1.txt
$ echo '222' > test2.txt
$ git add *.txt
$ git commit -m 'first commit msg'
$ git checkout -b dev1
##修改test1.txt
$ git add *.txt
$ git commit -m 'second commit msg'
$ tree .git
```

```shell
.git
├── COMMIT_EDITMSG #更改 commit2的msg(最新commit的msg)
├── HEAD #未变ref: refs/heads/dev1(目前工作区的分支名)
├── config
├── description
├── hooks
│   ├── applypatch-msg.sample
│   ├── commit-msg.sample
│   ├── fsmonitor-watchman.sample
│   ├── post-update.sample
│   ├── pre-applypatch.sample
│   ├── pre-commit.sample
│   ├── pre-merge-commit.sample
│   ├── pre-push.sample
│   ├── pre-rebase.sample
│   ├── pre-receive.sample
│   ├── prepare-commit-msg.sample
│   └── update.sample
├── index #未变 test1-version2和test2(gita dd后的 对象类型100644、key值和名称)
├── info
│   └── exclude
├── logs#!!日志
│   ├── HEAD ##每次commit的key值 切换分支信息、和commit的备注
│   └── refs
│       └── heads
│           ├── dev1#parentcommitkey commit-key author linus时间 +0800时区 commit备注
│           └── master #master分支上的commitparent commit(每次)
├── objects#所有的commit、blob、tree的SHA-1值前两位为文件夹名，后38位为文件名
│   ├── 25
│   │   └── 44eb966892404230c44a9c31632c84b60cf827 #tree-v1commit
│   ├── 4a
│   │   └── 3a65275241fdb7ed16030157461f0e1b4f4ea0 #!!tree2-v2commit
│   ├── 58
│   │   └── c9bdf9d017fcd178dc8c073cbfcbb7ff240d6c #test1-v1
│   ├── 96
│   │   └── ba9cc40b8dfc50f1bec6c695a9e1d2947025a3 #commit1-commit
│   ├── 9e
│   │   └── 74a5fe1f95a3b634fd8d13bbbb504eabfe740c #!!!commit-v2
│   ├── b7
│   │   └── dee1fd2f63786ec9e89b42cfe80e0d2fb9552e #!!test1-blob-v2
│   ├── c2
│   │   └── 00906efd24ec5e783bee7f23b5d7c941b0c12c #test2-blob
│   ├── info
│   └── pack
└── refs #引用
    ├── heads #文件夹，指向最新
    │   ├── dev1 #commit2的key值(dev1上最新commit)
    │   └── master #commit1的key值（master分支上最新commit)
    └── tags

18 directories, 30 files
```





## 一、类和数据域

#### 主类（main class）：

用于分析命令行参数启动javagit对象实现命令行要求



#### A.Repository class：

当前工作目录下，操控所有操作

- 数据域

  1.`File CWD` 当前工作目录

  2.`FIle REPO_FOLDER`静态文件对象指向.javagit文件夹

  3.`File HEAD_PATH` 头文件路径

#### B.Reference class (javagit/refs)：

所有的分支名和他们对应的SHA-1值

- 数据域

  1.`String REF_FOLDER` <!--目前引用的文件夹？？-->

  2.`HashMap branches` 从分支名映射到SHA-1值

#### C.Stage class(javagit/index)

此类用于管理javagit的staging area(javagit/index索引区)

- 数据域
  1. `HashMap _removedFiles` 从删除的文件名映射到它们的blob ID
  2. `HashMap _stagedFiles` 从已add未commit的文件名映射到它们的blob key
  3. `String STAGE_FOLDER` staged文件所在的文件夹
  4. `String INDEX_PATH` 索引文件的路径

#### D.Commit class

- 数据域

  1.`String _header` commi对象目前所在

  2.`String _parentId` 前次commit的key

  3.`String _secondParentId` <!--第二次提交对象的SHA-1值？？？-->

  4.`String _msg` commit的备注消息

  5.`HashMap _blobs` 将blob文件名映射到其SHA-1值

  6.`String _timestamp` 提交时间戳

  7.`File COMMITS_FOLDER` 提交对象所在的文件夹

#### E.KeyValueObject class

对象的磁盘内容的表示，路径：**.javagit/objects**

- 数据域
  1. `File BLOBS_FOLDER`  blob文件所在的文件夹
  2. `String _header`  blob对象指针
  3. `Byte[] _content` blob文件内容



## 二、类和方法

#### 主类：

1.main()

- 分析命令行参数并解决错误
- <!--预处理-->
- 根据参数调用repository的静态方法

----

#### E.Blob

​	1.根据key值构造Blob对象

​	2.`genSavaKey( )`

- 生成SHA-1值

- 将Blob内容保存到具有SHA-1值为文件名的文件中

  3.`diff()`

- 比较两个对象之间内容的差异

#### D.Commit

​	1.根据commit的key来构造Commit对象

​	2.`genSavekey( )`

- 生成SHA-1值

- 将Commit内容保存到具有SHA-1值为文件名的文件中

  3.`getParentid( )`

  4.<!--getSecondParent()??-->

  5.toString()

- 生成log日志格式

  6.`getBlodID(String filename)`

  - 根据文件名返回Blod id

#### A.repository class(javagit仓库)

​		**1.init():**

- 创建.javagit文件夹

- 初始化staging area(index)

- 初始化reference（javagit/refs）

- 创建头文件，并初始设置为“master”

  **2.add():**

- <!--获得当前 commit的blob？？-->

- 比较目前文件的SHA-1值和在objects中存储好的SHA1值

  - 如果一样，尝试从stagedFiles中删除
  - 如果不一样，在staged folde中保存新的blob文件，去掉旧的blob文件，替换成映射的新key值
  - 存入index中

  **3.commit():**

  - 根据当前 HEAD 构造新的commit对象，设置父指针

  - 针对每一个staging area中的条目

    - 如果commit，将其从staging area中移到commits area中

    - 如果removed，删掉这个旧的 SHA-1值

  - 清空staging area

  - 改变当前分支的引用

  - 保存暂存区

  - 保存commit对象，更新branch信息

  4.rm()

  5.log()

  6.status()

  7.checkout()

  8.branch()

  9.rm-branch()

  10.merge()



#### C.Stage class(javagit/index)

​	1.addFile():

- 获得文件的SHA-1值

-----



1. javagit add[file]：
   - 如果add staged文件，将文件的SHA-1值存储到index_staged文件中
   - 如果add remove文件，将文件的SHA-1值存储到index_removed文件中
2. javagit commit[file]：
   - 存储提交的日志、对Tree文件的reference
   - 将新版本的文件存储到objects文件中











