# Git的功能补充设计文档

#### A.repository class(javagit仓库)

 **1.init():**

- 创建.javagit文件夹
- 初始化staging area(index)
- 初始化reference（javagit/refs）
- 创建头文件，并初始设置为“master”

**2.add():**

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

**4.rm():**尝试从缓存区删除文件，如果缓存区不存在，则从工作区删除

**5.log():**从最新的reference查看最新commit的内容，根据提commit object的parent ref向后遍历，直到初始提交

**6.status()**

**7.checkout():**

- `checkout -- filename`

  - 从最新提交获取文件名的SHA-1值

  - 从 `.gitlet/blobs/[SHA-1 value]`获取文件内容

  - 读取文件内容并重写wd中的相应文件

- `checkout [commit id] -- [file name]`

  - 遍历`.gitlet/commits`目录，找到commit文件

  - 读取blob内容并重写wd中的对应文件

- `checkout [branch name]` +

**8.branch()**

- 创建分支：创建新指针，指向当前commit
- 切换分支：切换分支时，git将HEAD指向切换到的分支，并根据切换到的分支指向的commit替换当前工作区。因为每个文件的内容都被git存到了.git目录下的object中得到当前，且每个commit都指向了一个类似根目录的tree对象，根据新分支指向的commit即可得commit下所有的文件。
  - 根据commit key查询得到commit的value
  - 从commit value中解析得到根目录tree的key
    - 恢复(path)：
      根据tree的key查询得到value
      解析value中的每一条记录，即这个tree对象所代表的文内的子文件与子文件夹名称以及对应的blob/tree key
      对于blob，在path中创建文件，命名为相应的文件名，写入blob的value
      对于tree，在path中创建文件夹，命名为相应的文件夹名，递归调用恢复(path+文件夹名)

**9.rm-branch()：**确实被删除分支存在且不是主分支，删除指向分支的的reference

**10.git diff:**比较工作区与暂存区的不同git diff,比较两个commit之间的不同git diff <commit_id> <commit_id>,加号：在原 文件基础上添加了一行内容减号：删除了原文件的某一行

- 利用图搜索，以源文件字符，为横轴目标文件字符为竖轴构建图，插入是向下走，删除是向右走，特定位置的斜边依然代表没有操作，替换强行走斜边,用Myers算法找到从左上角到右下角的最短路径

**11.merge()：**

- 为当前分支和给定分支创建两个队列
- 将当前分支id和给定分支id添加到相应的队列中

- 创建hashset以记录访问的节点
- 使用bfs依次遍历当前分支的父级和给定分支的父级
- 检查hashset中是否存在同一个节点，如果有共同祖先节点，分别计算两个分支与共同祖先的diff,若diff不冲突,则自动合并

**12.reset** 回滚：修改HEAD的位置,即将HEAD指向的位置改变为之前存在的某个版本

**13.命令行交互**

- 要实现的命令

  - git commit 提交

  - git branch 查看当前已存在的branch

  - git checkout branchname

    切换到branchname分支

    若不存在，创建名为branchname的新分支

  - git log 查看当前分支的commit历史记录

  - git reset --hard commit

    回滚到对应的commit

- 两种实现

  - Scanner接收用户指令
  - 通过main函数命令行参数String[] args接收用户指令



