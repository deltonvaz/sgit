import java.io.FileNotFoundException
import java.lang.Long.parseLong
import java.security.AccessControlException

import better.files.File
import better.files.File.currentWorkingDirectory

import scala.util.matching.Regex


object Sgit {

  var head : File = currentWorkingDirectory
  var stage : File = currentWorkingDirectory
  var currentCommit = Some()

  var isFirstCommit : Boolean = false

  def init(): Unit = {
    FileHandler.systemVerify()
    FileHandler.createGitBaseFiles()
  }

  /*
    The method add consists in:
      1- Generate blob file
      2- Add blob file to INDEX (stage)
   */
  def add(param : String): Unit = {

    //Create switch for param (!!!)

    //Check if the file that is being added exists
    if (!FileHandler.fileExists(param)){
      throw new FileNotFoundException(s"$param not found")
    }

    val newFile = currentWorkingDirectory/param

    /*
      TODO Future to create tree with folders
    */
    val rx = """(?<!/)/(?!/)""".r.unanchored
    val containsPath = param match {
      case rx(_*) => true
      case _ => false
    }

    /*
      If the path contains folders git add must create tree files for each folder
      return a list of trees
     */
    //TODO
    //    if (containsPath) {
    //      generateTree(newFile)
    //    }

    //Creating blob file
    val length = newFile.size

    val content = newFile.contentAsString

    //Generate header for blob file
    //val headerLine = Object.HeaderLine(ObjectType.Blob, length)

    (currentWorkingDirectory/".sgit"/"objects"/newFile.sha1)
      .createIfNotExists()
      //.appendLine(headerLine)
      .appendLine(content)


    //Put file in the INDEX (stage) file
    /*
      First is needed a check to see if the file was modified or not
     */
    (currentWorkingDirectory/".sgit/INDEX").appendLine(newFile.sha1 + " " + param)

  }

  /*
    Commit should:
    1- Create new tree file with blobs/tree that are/is being added
    2- Create new commit file which points to the created tree
  */
  def commit(message : String) : Unit = {
    this.initialize()

    //Creating tree file using INDEX SHAs[Only with blob - aka considering all files as blob]

    //Get stagged files
    val oldStage = stage.lines

    //Get Blobs SHA1 from Stage
    var blobSHAs = Map[String, String]()
    oldStage.foreach {
      case Constants.STAGE_PATTERN(id, fileName) => {
        blobSHAs = blobSHAs + (fileName -> id) //fileName unique
      }
      case _ => println("Error reading staged files") //TODO throwError
    }

    //As Im only considering blob files
    val TempTree = Tree("", blobSHAs)

    //println(blobSHAs)

    //Create temporary tree
    val afl = File.newTemporaryFile().appendLine(TempTree.treeHeader)

    var realTree = Tree("", blobSHAs)

    var treeObj:File = Constants.OBJECTS_FOLDER/afl.sha1



    //Creating tree obj
    println(treeObj.path + " " + afl.sha1)



    //if(!treeObj.exists) {

    treeObj = treeObj.createIfNotExists().append(realTree.treeHeader)
    //}

    println(realTree.treeHeader)
    //TODO Read parent commit details

    /*
      Create commit file
     */

    /*
      Creating new commit file
    */

    //Default if first commit
    var headFile = "master"
    var parentCommitSHA = "NONE"

    if (!isFirstCommit) {
      println("Not first commit anymore")
      // get parent branch from refs
      //Pega o commit q tem no head e coloca no novo commit
      val headCommit = (currentWorkingDirectory/".sgit"/Constants.DEFAULT_HEAD_PATH).lines.head
      //Pega o headCommit SHA
      parentCommitSHA = headCommit

//      var parentCommitInfo = (Constants.OBJECTS_FOLDER/headCommit).lines.head
//      println(parentCommitInfo)
//
//
//      parentCommitInfo match {
//        case Constants.COMMIT_PATTERN(treeid, parentSha, b, c) => {
//          println(parentSha)
//
//        }
//        case _ => println("Error reading staged files") //TODO throwError
//      }
      //Check branch
    }


    println("Novo tree obj " + treeObj.sha1)
    val commit = Commit(afl.sha1, parentCommitSHA, message)

    val tempCommit = File.newTemporaryFile().appendLine(commit.commitHeader)

    parentCommitSHA = tempCommit.sha1

    (currentWorkingDirectory/".sgit"/"objects"/tempCommit.sha1)
      .createIfNotExists()
      .appendLine(commit.commitHeader)

    //HEAD points to master branch
    (currentWorkingDirectory/".sgit/HEAD")
      .clear
      .appendLine("refs/heads/"+headFile)
      //.appendLine("ref: refs/heads/"+headFile)

    //Create commit file which head points to
    (currentWorkingDirectory/".sgit"/"refs"/"heads"/headFile)
      .createIfNotExists()
      .clear
      .appendLine(parentCommitSHA)



    //val refCommit = (currentWorkingDirectory/".sgit"/"refs"/"heads"/message)
    //        .createIfNotExists()
    //        .appendLine(commit.commitHeader)

    var outmessage : String = ""

    outmessage = s"[master (root-commit) ${tempCommit.sha1}] $message\n"+
      s" ${blobSHAs.size} files changed, ${blobSHAs.size} insertions(+)\n" //haaaaa

    var newFiles : String = ""

    blobSHAs.foreach(f =>{
      newFiles = " create "+f._2+"\n"+newFiles
    })

    outmessage = outmessage+newFiles

    println(outmessage)


  }

  def status() : Unit = {
    initialize()
    var message: String = ""

    /*
      Get current branch
     */

    val head = (currentWorkingDirectory/".sgit/HEAD").lines

    if (head.head == Constants.DEFAULT_HEAD_PATH) message = s"On branch master\n\n"
    else message = s"On branch $getCurrentBranch\n\n"


    if(isFirstCommit) message = message + "No commits yet\n\n"

   /*
      Check changes to be commited
     */
    //TODO if firstCommit check only index(staged files

    if(!(getStagedFiles() == getCurrentCommitFiles())){

      message = message + "Changes to be commited:\n\n"
      var stagedFileNames = getStagedFiles()
      stagedFileNames = stagedFileNames.diff(getCurrentCommitFiles())
      var stagedFileNamesString:String = ""
      stagedFileNames.foreach(file => {
        stagedFileNamesString = s"\t\tnew file:\t$file\n"+stagedFileNamesString
      })
      message = message + stagedFileNamesString + "\n"
    }else{
      message = message + "nothing to commit, working tree clean"
    }

    /*
      Check untracked and changed files
     */

    //Start getting files in root dir
    var repFiles: Set[String] = Set()
    currentWorkingDirectory.list.foreach(f => {
      repFiles = repFiles.+(f.name)
    })

    repFiles = repFiles.diff(Constants.IGNORE_ROOT_FILES) //Arquivos do working directory

    //End of getting files in root dir

    //repFiles.foreach(f => println(f))


    //Start getting files in stage area
    val staged = (currentWorkingDirectory/".sgit/INDEX").lines

    var stagedFiles: Set[String] = Set()
    var stagedFilesWithSHA : Map[String, String] = Map()
    staged.foreach(file => {
      val StagePattern: Regex = """(\w+) +(\w+.+)""".r
      file match {
        case StagePattern(sha1, fileName) => {
          if (repFiles.contains(fileName)) {
            stagedFiles = stagedFiles.+(fileName)
          }
          stagedFilesWithSHA = stagedFilesWithSHA + (fileName -> sha1)
        }
      }

    })

    val modifiedFiles = getModifiedFiles(getCurrentCommitFiles(), stagedFilesWithSHA)

    if(modifiedFiles.nonEmpty)
      message = message + "Changes not staged for commit:\n\n" + modifiedFiles.mkString("\t\t", "", "\n") + "\n"


    val newFiles = repFiles.diff(stagedFiles) //Check new files



//    println("New files")
//    newFiles.foreach(f => println(f))


    if(newFiles.nonEmpty){
      message = message + "Untracked files:\n\n"
      var untrackedFiles: String = ""
      newFiles.foreach(fileName => untrackedFiles = "\t\t"+fileName+"\n"+untrackedFiles)
      message = message + untrackedFiles
    }


    println(message)


  }

  def tempSha(content : String) : String = {
    ""
  }

  def getStagedFiles() : Set[String] = {
    val staged = (currentWorkingDirectory/".sgit/INDEX").lines
    var stagedFiles: Set[String] = Set()
    staged.foreach(file => {
      val StagePattern: Regex = """(\w+) +(\w+.+)""".r
      file match {
        case StagePattern(sha1, fileName) =>
          stagedFiles = stagedFiles.+(fileName)
      }
    })
    stagedFiles
  }


  def getModifiedFiles(commitedFiles : Set[String], stagedFiles : Map[String, String]) : Set[String] = {
    var shaWorkingDir : String = ""
    var modifiedFiles : Set[String] = Set()

    commitedFiles.foreach(fileName => {
      shaWorkingDir = (currentWorkingDirectory/fileName).sha1
      if(!stagedFiles.exists(_ == (fileName -> shaWorkingDir))) {
        modifiedFiles = modifiedFiles.+(fileName)
      }
    })

    modifiedFiles

  }


  def getCurrentCommitFiles() : Set[String] = {
    var tree = ""
    var commitFiles: Set[String] = Set()

    val headCommit = (currentWorkingDirectory/".sgit"/Constants.DEFAULT_HEAD_PATH).lines.head
    //Pega o headCommit SHA

    var parentCommitInfo = (Constants.OBJECTS_FOLDER/headCommit).lines.head


    //Get tree
    parentCommitInfo match {
      case Constants.COMMIT_PATTERN(treeid, parentSha, b, c) => {
        tree = treeid
      }
      case _ => println("Error reading staged files") //TODO throwError
    }

    (Constants.OBJECTS_FOLDER/tree).lines.foreach(file => {
      file match {
        case Constants.STAGE_PATTERN(sha1, fileName) => {
          commitFiles = commitFiles.+(fileName)
        }
      }
      //commitFiles = commitFiles.+(file)
    })

    commitFiles
  }



  def getListOfSubDirectories(directoryName: String): Iterator[String] = {

    File(directoryName).listRecursively.filter(_.isDirectory).map(_.name)
//      .listFiles
//      .filter(_.isDirectory)
//      .map(_.getName)
  }

  def getCurrentBranch: String = {
    initialize()
    val headCommit = head.lines.head
    var retVal = ""
    headCommit match {
      case Constants.BRANCH_PATTERN(a, b) => {
        retVal = b
      }
      case _ => println("error ocurred while loading branch") //TODO throwException
    }
    retVal
  }

  def generateTree(file : File) : Unit = {
    //Cria as trees
    var path = file.parent
    while(path != currentWorkingDirectory) {
      //println("Mostrando pai: "+path.toString())


      path = path.parent
    }

    //println("parent " + path.parent.toString())
  }


  //Main -> initialize() -> load()
  //initialize = verifica as condicoes basicas para a construcao do sistema
  //load carrega em memoria a descricao do sistema na forma de tree-blobs
  def initialize(): Unit = {
    if(!FileHandler.hasPermission) throw new AccessControlException("Not allowed to initialize Sgit")
    head = currentWorkingDirectory/".sgit/HEAD"
    stage = currentWorkingDirectory/".sgit/INDEX"
    isFirstCommit = head.isEmpty
  }



}

object Main extends App {
  println("Hello from main Sgit")
}
