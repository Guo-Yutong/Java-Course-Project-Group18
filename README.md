# Java-Course-Project-Group18

**此项目为Java18小组的课程项目**

拟用java实现部分git命令的版本控制工具

## 课程项目要求

- 命令行工具
- 参考git实现原理，实现blob,tree,commit核心存储结构

**功能点**：

- 可以提交commit，可以进行“git log”查看commit历史
- 可以进行“git reset”回滚到指定commit
- 可创建多分支，可在分支之间切换

- 时间充足可考虑merge功能(加分)、远程仓库功能(不要求)

-----

### Git实现原理

- Key-value存储
  - value：Object的内容
  - Key：Object内容的hash
- Object的三种类型
  - Blob：文件 <!--每个用户文件的每个版本都会对应一个blob对象-->
    - Blob的Value：文件内容，没有文件名信息
    - Blob的Key：文件内容的hash值
  - Tree：文件夹 <!--每个用户文件夹（和子文件夹）都会对应一个Tree对象-->
    - Tree的Value：1.子文件夹和子文件**名称**；2.每个子文件Blob key；3.每个子文件夹tree的key；
    - Tree的Key：以上内容的hash值
  - Commit：提交 <!--每次提交对应一个Commit对象-->
    - Commit的Value：1.项目根目录tree对象的key；2.前驱commit对象的key；3.代码author；4.代码commiter；5.commit时间戳；6.commit备注/注释；
    - Commit的Key：以上内容的hash值
- Git object的存储
  - 每个object存成一个文件
    - 在`.git/objects`目录下
    - 文件夹名+文件名就是key值
    - Value被编码进了文件内容，暂时不用了解

----

### 开发方式

- 每个组长创建一个github仓库，添加组员的push权限
- 永远使用Pull Request来更新主分支
- Commit描述和PR描述尽可能详细
- 使用issue来讨论/记录开发计划、分工及问题/bug