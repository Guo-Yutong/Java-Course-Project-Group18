# Gitlet Design Document

**Name**: Jiarui Li

## Classes and Data Structures

### *Main class*

This class parses the command line arguments starts up a gitlet object to serve the command.

### *Repository class*

This class represents the repository in current working directory and controls all operations.

+ Fields
    1. `File CWD` current working directory
    2. `FILE REPO_FOLDER` static FILE object points to .gitlet folder
    3. `File HEAD_PATH` head file's path

### *Reference class*

Maintain all mappings from branch name to their corresponding SHA-1 value.

+ Fields
    1. `String REF_FOLDER` folder that references live in
    2. `HashMap branches` Mapping from branch name to SHA-1 value.

### *Stage class*

This class manages the staging area.

+ Fields
    1. `HashMap _removedFiles` Mapping from removed filenames to their blob id
    2. `HashMap _stagedFiles` Mapping from staged filenames to their blob id
    3. `String STAGE_FOLDER` folder that staged files live in
    4. `String INDEX_PATH` path of index file

> Modifications Not staged for commit
> Untracked files

### *Commit class*

Representation of the commit object

+ Fields
    1. `String _header` commit object header
    2. `String _parentId` SHA-1 value of parent commit object
    3. `String _secondParentId` SHA-1 value of second parent commit object
    4. `String _msg` commit message
    5. `HashMap _blobs` mapping of blob filename to its SHA-1 value
    6. `String _timestamp` timestamp of commit
    7. `File COMMITS_FOLDER` folder that commit object live in

### *Blob class*

Representation of the on-disk content of a blob object, path: **.gitlet/blob**

+ Fields
    1. `File BLOBS_FOLDER` folder that blob files live in
    2. `String _header` blob object header
    3. `Byte[] _content` content of a blob file.

## Algorithms

### *Blob*

1. fromFile()
    + construct Blob object according to blob id
2. saveFile()
    + generate SHA-1 value
    + save Blob content into file with SHA-1 value as filename
3. diff()
    + compare difference between two blob file's contents

### *Commit*

1. fromFile()
    + construt Commit object according to commit id
2. saveFile()
    + generate SHA-1 value
    + save Commit conten into file with SHA-1 value as filename
3. getParentId()
4. getSecondParentId()
5. toString()
    + generate log format
6. getBlobId(String filename)
    + return Blob id accordint to filename

### *Main*

1. main()
    + parse arguments and handle error
    + validate precondition
    + call static method of repository according to argument

### *Repository*

1. init():
    + otherwise create .gitlet directory
    + init staging area
    + init reference
    + create head file and write "master" to it

2. add():
    + get current commit's _blobs
    + compare current file's SHA-1 value with file's SHA-1 value in _blobs
        1. same, try remove it from current stage's stagedFiles
        2. not same, save file's new blob file in stage folder, remove old blob file and put new mapping into stage's stagedFiles or delete the mapping in stage's removedFiles
    + save stage into index file

3. commit():
    + copy construct a new commit object according to current HEAD, set parent pointer
    + for every entry in current staging area
        + if added entry, then move blob file from staging area to commits area, set new commit reference
        + if removed entry, then delete old SHA-1 value
    + clear staging area
    + change current branch's reference
    + save stage
    + save commit object and update branch info

4. rm():
    + if file is staged, remove
    + if file is in current commit, remove working directory's file and add removed filename into staging area.

5. log():
    + from head ref read construct commit object, print relative info
    + traverse backwards according to commit object's parent ref field until initial commit

6. global-log():
    + iterate all commits, print their metadata info.

7. find():
    + iterate all commits, if `commit message` equals to the msg in file, then print out commit id

8. status():
    + construct index object according to the index file
    + print relative info use index object method

9. checkout():
    1. `checkout -- filename`
        + from head commit get filename's SHA-1 value
        + get the file content from `.gitlet/blobs/[SHA-1 value]`
        + read the file content and overwrite the corresponding file in wd
    2. `checkout [commit id] -- [file name]`
        + iterate `.gitlet/commits` directory, find the commit file
        + read the blob file content and overwrite the correspondint file in wd
    3. `checkout [branch name]`
        +

10. branch():

11. rm-branch():

12. reset():

13. merge():
    1. get lca commit
        + create two queues for current branch and given branch
        + add current branch and given branch's id into corresponding queue
        + create a hashset to record visited node
        + use bfs to in turn traverse current branch's parent and given branch's parend
        + check if one node exists in hashset
            + if so, get lca commit

### *Stage*

1. addFile():
    + get file's SHA-1 value


## Persistence

1. gitlet add [file]:
    + if add staged file, store SHA-1 value of file into index_staged file
    + if add remove file, store SHA-1 value of file into index_removed file

2. gitlet commit [file]:
    + store commit's log, metadata and reference to Tree file 
    + store new version of files into blob files