# 关于init、add、commit的详细设计

- 错误情况报错：
  - 如果用户未输入任何参数，则打印消息 `Please enter a command.`并退出。
  - 如果用户输入的命令不存在，请打印该消息`No command with that name exists.`并退出。
  - 如果用户输入的命令的操作数数量或格式错误，请打印该消息`Incorrect operands.`并退出。
  - 如果用户输入的命令要求位于已初始化的Gitlet工作目录（即包含`.gitlet`子目录的目录）中，但不在该目录中，则打印该消息`Not in an initialized Gitlet directory.`

## 命令

#### init

- 命令：`git.Main init`
- 具体描述：该命令会在在当前目录中创建一个新的Gitta版本控制系统，然后自动创建并分配`master`分支为当前分支。这个命令自带initial commit 的message，也就意味着之后在这个系统下所有commit都可以回溯到这个initial commit
- 报错情况：如果当前目录中已经存在一个版本控制系统，则应该中止init,不可以同新的版本控制系统覆盖现有的系统，此时打印错误信息`A Gitta version-control system already exists in the current directory.`

#### add

- 命令：`java git.Main add [file name]`
- 具体描述：复制本地文件，并将复制后的文件添加到stage。如果stage里已有该文件，则新的复制文件将覆盖旧文件。如果文件的当前工作版本与当前commit中的版本相同，则不用往stage里进行add操作，如果已存在于stage中则从stage中将其删除（更改，添加，然后改回来的时候可能会发生该情况）。如果文件已被标记为to be removed（参考`gitlet rm`），就删除该标记。
- 报错情况：如果文件不存在，打印错误消息`File does not exist.`并退出不做任何更改。

####  commit

- 命令：`java gitlet.Main commit [message]`

- 具体描述：将指定文件的快照保存在当前commit和staging area，以便之后可以恢复这些文件，创建新的commit来track。默认情况下，每个提交的文件快照将与其parent commit的文件快照完全相同，不更新而是完全保留文件的版本。commit只会更新commit时已暂存的正在track的文件，在这种情况下，提交包含已暂存的文件的版本，而不是从其父级获取的文件的版本。commit将保存并开始track所有已暂存但未被其父级track的文件。最后，因为`rm`命令，在当前commit中track的文件可能在新commit中untrack。

  默认情况下，commit与其parent commit相同。暂存和删除的文件是对commit的更新。

  有关提交的一些其他要点：

  - commit后将清除staging area。
  - commit命令从不添加，更改或除去工作目录中的文件（`.Gitta`目录中的文件除外）。该 `rm`命令*将*删除此类文件，并以某种方式将其标记为untrack的`commit`。
  - 在暂存或删除之后对文件所做的任何更改都将被该`commit`命令忽略，该 命令*只会*修改``.Gitta`` 目录的内容。例如，如果使用`rm`命令删除track的文件，这对下一次提交没有影响，下一次提交仍将包含该文件的已删除版本。
  - 在commit命令之后，新提交将作为新节点添加到commit tree中。
  - 刚进行的在commit命令之后，新commit将作为新节点添加到commit tree中。成为“current commit”，并且头指针现在指向它。先前的head commit是该提交的parent commit。
  - 每次提交都应包含进时间戳。
  - 每个commit 都有一条与之关联的log消息，该消息描述了提交中文件的更改。这由用户指定。整个消息应仅占用`args`传递给的数组中的一项`main`。要包含多字消息，您必须将其用引号引起来。
  - 每个提交都由其SHA-1 ID标识，该ID必须包括其文件的文件（blob）引用，父引用，日志消息和提交时间。