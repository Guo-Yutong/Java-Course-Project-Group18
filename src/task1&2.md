### 12月3-12月10 第一周任务：

#### task1.实现key-value存储

- 最简单的key-value存储方式
  - key作为文件名，文件内容作为value
- 支持以下功能
  - 给定value，向存储中添加对应的key-value
  - 给定key，查找得到对应的value值
- 封装成class对外提供接口
- 单元测试

#### task2.将一个文件夹转化为key，value

- 给定一个文件夹目录，将其转化成若干tree和blob
  - 深度优先遍历此目录
    - 遇到自文件就转化为blob并保存
    - 遇到子文件夹就递归调用文件夹内部的子文件/文件夹最后构造tree并保存
- 使用task1提供的接口
- 单元测试

-----

#### 想法：

- 先实现一个**求hash值的类：**

  - 数据域：
    
    - hash值转为的16进制字符串；
  - 构造方法：
    - 参数为文件路径：以FileInputStream类新建一个此类
    - 参数为字符串

  - 提供方法：

    - 参数为文件路径时，返回值为字节数组

    - 参数为字符串，得到字符串的hash值字节数组

    - 将得到的hash值字节数组转为16进制字符串

      

- 考虑到tree和blob都是key-value存储，只是它们的具体内容不一，考虑到有“is-a”关系，可以设立一个**KeyValueObject父类**

  - 数据域：
    - 类型（Blob，Tree）
    - key值 
    - 路径值 File对象
  - 构造方法：由子类实现

  - 其方法有：
    - 生成Key方法：1.参数为文件路径，得到hash值；2.参数为字符串，得到hash值
    - copy方法：读取现有的文件数据，copy出一份以key为名字的文件



- **Blob类**：继承了KeyValueObject父类
  - 构造方法：生成key，类型设为blob
  - 方法：
    - getType，getKey
    - 重写toString方法加上”100644 blob“信息
- **Tree类**：继承了KeyValueObject父类
  - 构造方法：类型设为Tree，深度优先遍历文件夹，不断更新value内容，根据tree的value生成Key
  - 方法：
    - getKey、getType
    - 重写toString方法，加上“040000 tree“信息
    - 重写copy方法：创建以key为名的文件，将更新完毕的value内容写入其中
- **Commit类**：继承KeyValueObject父类
  - 数据域：1.项目根目录tree对象的key； 2.前驱commit对象的key；3.代码author；4.代码commiter；5.commit时间戳；6.commit备注/注释；7.此次commit的key
  - 



