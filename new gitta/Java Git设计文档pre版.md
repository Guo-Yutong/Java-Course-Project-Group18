# Gitta设计文档

**@author：java18小组**

## 一、类和数据域

#### 主类（main class）：

用于分析命令行参数启动Gitta对象实现命令行要求

- 数据与：

  1.`CWD` 当前工作目录

#### A.Repository class：

当前工作目录下，操控所有操作

- 数据域

  1.`File CWD` 当前工作目录

  2.`FIle REPO_FOLDER`静态文件对象指向`.gitta`文件夹

  3.`File HEAD_PATH` 头文件路径，即`.gitta/head`文件，内容为当前所指分支
  
  4.`_reference` 当前仓库
  
  5.`_head`当前仓库的分支名
  
  6.`_lastCommit`最新一次的Commit
  
  7.`_Stage`当前仓库的暂存区

#### B.Reference class：

代表磁盘上的分支内容、commit内容

所有的分支名对应他们的SHA-1值

- 数据域

  1.`String REF_PATH ，即`.gitta/refs文件

  2.`HashMap _branches` 从分支名映射到SHA-1值

#### C.Stage class

此类用于管理仓库的暂存区

- 数据域
  1. `STAGE_FOLDER`stage文件夹的路径，即`.gitta/stafe`文件夹
  2. `INDEX_PATH`index文件的路径,即`.gitta/index` 文件
  3. `HashSet _removedFiles` 从删除的文件名映射到它们的ID
  4. `HashMap _stagedFiles` 从暂存的文件名映射到它们的ID
  5. `String STAGE_FOLDER` staged文件所在的文件夹
  6. `String INDEX_PATH` 索引文件的路径

#### D.Commit class

- 数据域

  1.`String _header` commi对象目前所在

  2.`String _parentId` 前次commit的key

  3.`String _secondParentId`

  4.`String _msg` commit的备注消息

  5.`HashMap _blobs` 将blob文件名映射到其SHA-1值

  6.`String _timestamp` 提交时间戳

  7.`File COMMITS_FOLDER` 提交对象所在的文件夹

#### E.Blob class

表示Blob对象在磁盘上的内容，路径：.gitlet / blob

- 数据域

  1.`File BLOBS_FOLDER` Blob文件所在的文件夹

  2.`String _header` Blob对象标头

  3.`Byte[] _content` Blob文件的内容。

## 二、类和方法

#### 主类：

1.main()

- 分析命令行参数并解决错误
- 检核是否合规validate
- 根据参数<u>调用repository的静态方法</u>

------

#### E.Blob

 1.根据key值构造Blob对象

 2.genSavaKey( )

- 生成SHA-1值
- 将Blob内容保存到具有SHA-1值为文件名的文件中

 3.diff()

- 比较两个对象之间内容的差异

#### D.Commit

 1.fromFile()

- 根据commit的key来构造Commit对象

 2.genSavekey()

- 生成SHA-1值
- 将Commit内容保存到具有SHA-1值为文件名的文件中

3.getParentid()

4.getSecondParentId()

5.toString()

- 生成log日志格式

6. getBlodID(String filename)

- 根据文件名返回Blod id

#### A.repository 

 **1.init():**

- 如果该目录下没有版本管理仓库，创建`.gitta`文件夹
- 初始化暂存区
- 初始化reference
- 在`.gitta/head`（HEAD_PATH)里写入master

**2.add():**

- 获取_lastCommit的objects
- 比较目前文件的SHA-1值和在_lastCommit中已有的Objects对象的SHA-1值
  - 如果一样，并且是与暂存区的文件一样，则尝试从stagedFiles（暂存文件）中删除
  - 如果一样，并且是与已删除区域的文件一样，则尝试从删除中取出。
  - 如果不一样，则去掉旧的Objects，替换成新的值，并将stage存入到index文件中。

**3.commit():**

- 根据当前 HEAD 构造新的commit对象，设置父指针
- 针对每一个staging area中的每个操作，如果是add，将object从暂存区中移到已经commit的区域中，生成新的commit对象
- 清空暂存区
- 改变当前分支的引用，ref指向新的commit对象
- 生成一个commit对象到文件commits中
- 保存commit对象，更新branch信息

**4.rm()**

- 如果文件已add，将文件从暂存区移到UntrackedFiles区域

- 如果文件已经commit，则删除工作目录的该文件，并将被删除的文件名添加到Removed Files区域

**5.log()**

- 从头引用读取构造的commit对象，打印相关信息

- 根据提commit object的parent ref往回遍历，直到初始commit

**6.global-log()**

- 迭代所有commits，打印其相关信息。

**7.status()**

- 调用当前工作的Stage的方法打印相关信息

**8.checkout()**

- `checkout [branch name]` 切换分支
  - 由branchname得到它的最新commit对象，与当前工作目录最新分支比较，没有的Object删掉，其他的恢复到工作区
  - 保存到head文件中
  - 清空Stage

**9.branch()**

- 通过一个给定的branch名来调用`_reference.addBranch`方法来创建分支。

**10.rm-branch()**

确认被删除分支存在且不是主分支，删除指向分支的的reference

~~~java
public static void removeBranch(String branch) throws GitletException {
        checkBranchExist(branch);
        if (_head.equals(branch)) {
            throw new GitletException("Cannot remove the current branch.");
        }
        _reference.removeBranch(branch);
    }
~~~

**11.reset()**

根据输入的回滚的commit id修改HEAD的位置,即将HEAD指向的位置改变为对应的版本

- checkoutCommit(id)通过id返回一个commit对象，所有的Objects组成一个Hashset，与当前分支最新的Objects比较，没有的删掉，有的就恢复到工作目录上，并清空Stage
- 更新当前的commit-id，根据id修改分支名

~~~java
        Commit commit = Commit.getCommit(id);
        if (commit == null) {
            throw new GitletException("No commit with that id exists");
        }
        if (!isValidCommitCheckout(id)) {
            throw new GitletException("There is an untracked file in the way;"
                                  + " delete it, or add and commit it first.");
        }
        checkoutCommit(id);
        _reference.branchUpdate(_head, id);
    }

~~~

**12.merge()**

- 确认要merge的branch是否存在，确认此branch不是目前的分支
- 若splitID和branch最新commitid一致，则拟合并的branch即是当前的祖先
- 若splitID和目前分支一致，则需要目前分支fast-forward，使用chekoutCommit方法，将没有的Object实现在当前工作目录中

- 其余情况调用handleMerge方法，在没有冲突的情况下
  - 若分支修与lca相比修改过而当前分支未修改，直接在工作区和暂存区中生成这些文件
  - 若分支上有新文件而当前分支没有，处理同上行
  - 若当前分支与lca一致，所给分支有删除文件，则需要删除工作区的对应文件
- 其余情况需要处理冲突
  - 读取两个分支上的冲突文件内容，并按一定格式写入文件
- merge成功后生成新的commit对象，如果有处理冲突，输出有merge冲突的信息

#### B.Reference class：

1.getReference():

读取可序列化的refs文件生成reference类

2.getBranch(branchName):

若哈希表_branches中有这个分支名则返回其value哈希值，没有则返回null

3.init():

调用getReference(),并在哈希表中加入master分支名和此初始化的commitid

在refs文件中写入此reference

4.branchUpdate(name,id):之前存在的更新并存入refs中，addBranch(name,id)：之前不存在的put，removeBranch

5.getBranchNames():取得所有的名字

6.containsBranch(branch):返回是否含有此名字的分支

#### C.Stage

1. init()：生成stage文件夹
2. getStage():从index中读取生成Stage对象
3. isEmpty():判断暂存区是否为空
4. getRemovedFiles():返回相应
5. getStagedFiles():返回相应
6. getStagedFileNames():_stagedFiles.keySet()即返回所有暂存区文件名，即hashmap的key
7. getRemovedFileNames():
8. isFileStaged(file)、(filename):此文件是否在暂存区
9. isFileRemoved(file)、(filename):此文件是否被标记Removed
10. unstageFile(file):把文件从暂存区移走并写入index中

11.unremovedFile(file):把文件从removedFiles的HashSet中去除

12.fromFile():从index文件中读取一个Stage类

13.save():写入一个对象到index中

14.clear():所有_stagedFiles中的，如果在stage文件包中存在，则删去

15.addFile(file)：

- 若stagedFiles中有这个文件，则比较两者sha-1值

  - 若一样则return

  - 若两者sha-1不一样，则删掉旧sha-1值

- stagedFiles中没有这个文件，则往_stagedFiles中put入这个新的k-v

- 把object写入stage/sha-1文件夹中，file写入index中

　

