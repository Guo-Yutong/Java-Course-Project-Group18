# java pre注意事项	

##### 1.课程项目要求

- 打分：助教打分，综合pre、github上的代码、以及文档，来综合打分

- 顺序

  <img src="C:/Users/21593/AppData/Roaming/Typora/typora-user-images/image-20210106153108605.png" alt="image-20210106153108605" style="zoom:50%;" />

1. 课程项目的考核标准会是以下几点：
   1. 功能实现情况（基础功能是否实现，额外功能是否实现）
   2. 代码模块划分、类/接口设计情况
   3. 代码文档、注释、单元测试情况
   4. 小组多人合作情况（根据issue、pr以及commit历史）
   5. 一些细节实现方面的亮点

   每个小组还需要提交一份项目报告word文档，内容主要包括：项目github链接以及最终版本的commit id，已有的设计文档整合与补充，小组分工合作情况，课程项目感受与收获。1月10日24点前发到我的邮箱zhangzhiyi@pku.edu.cn

-----



注意：

- pre注意事项：代码底色换成浅色，pre时候语速适当加快。

- 项目备忘录：

  - classpath使得不同目录下都可以调用这个class文件进行命令行交互

  - 我们的add实现和reset时是否能reset到别的分支上，是属于soft还是默认还是hard
  - 可以弄一下 java gitlet.Main help 说明下所有使用情况

## 2.pre流程设想

- **功能实现情况**（**区分**基础功能和额外功能）

  - 文字展示：

    - 基础功能和额外功能划分<img src="C:\Users\21593\AppData\Roaming\Typora\typora-user-images\image-20210106143545895.png" alt="image-20210106143545895" style="zoom:80%;" />

    - 更细一步展示可用的命令行 ，eg.`git log`--查看当前分支的历史记录

  - 功能展示视频，后期标注上大小标题

    - 展示页面：一半页面放命令行，一半页面放仓库页面，直观展现命令行会让仓库发生什么变化
    
      尹健组有做类似的视频：
    
      <img src="C:/Users/21593/AppData/Roaming/Typora/typora-user-images/image-20210106153835002.png" alt="image-20210106153835002" style="zoom:50%;" />

- **代码模块划分、类/接口设计情况**
  - 总体设想+类图（标注颜色）
  - 展开讲解类，类里有什么方法

- **设计亮点**

- **现场测试**（老师说可以不用展示，可去除）

- ##### 提问环节

## 3.别的组做的好的地方

- 第三组：**流程图**

  <img src="C:\Users\21593\AppData\Roaming\Typora\typora-user-images\image-20210106142124080.png" alt="image-20210106142124080" style="zoom:50%;" />

- 小灵组，**类调用图**：

  <img src="C:\Users\21593\AppData\Roaming\Typora\typora-user-images\image-20210106145333489.png" alt="image-20210106145333489" style="zoom:60%;" />

  <img src="C:\Users\21593\AppData\Roaming\Typora\typora-user-images\image-20210106145751952.png" alt="image-20210106145751952" style="zoom:67%;" />

  - 尹健组

    - **IDE注释**（javadoc注释规范）

    ![image-20210106155950007](C:/Users/21593/AppData/Roaming/Typora/typora-user-images/image-20210106155950007.png)

    - 展示**流程的判定树**更好些

<img src="C:/Users/21593/AppData/Roaming/Typora/typora-user-images/image-20210106153525219.png" alt="image-20210106153525219" style="zoom:100%;" />

**结束页面**

<img src="C:/Users/21593/AppData/Roaming/Typora/typora-user-images/image-20210106154312320.png" alt="image-20210106154312320" style="zoom:67%;" />

- 陈鹏组：需要对diff算法改进，这块可以参考官方方法，降低时间复杂度
  - 陈诗组：**类和方法的阐述**

  <img src="C:/Users/21593/AppData/Roaming/Typora/typora-user-images/image-20210106163435371.png" alt="image-20210106163435371" style="zoom:67%;" />

- 敖海航组：四种回滚方式：reset(三种）（回滚并删除）和revert（回滚）,本地仓库回滚、本地和工作区同步回滚

亮点：

stage

merge

status

展望：





2.

序列化

merge

自己写的exception