import better.files.File
import misc.{BranchHandler, CommitHandler, Constants, FileHandler, Functions, StageHandler}
import objects.{Blob, Commit, Tree}

case class Sgit (var workingDirectory : String) {

  val workingDir : File = File(workingDirectory)
  var head : File = _
  val stage : StageHandler = StageHandler(workingDirectory)
  val branch : BranchHandler = BranchHandler(workingDirectory)
  val currentCommit : CommitHandler = CommitHandler(workingDirectory)
  val isFirstCommit : Boolean = CommitHandler(workingDirectory).isFirstCommit

  /**
    * function that initializes the sgit system
    * */
  def init(): Unit = {
    FileHandler(workingDir).systemVerify()
    FileHandler(workingDir).createGitBaseFiles()
  }

  /**
    *
    * function responsible for adding files in the staging area
    *
    * @param param filename(s) / . / regex
    */
  def add(param : String): Unit = {
  //Check if the file that is being added exists
  if ((workingDir/param).notExists) {
    println(Console.RED + s"$param not found")
    return
  }
  //Create blobs for everything
  if(param == "."){
    workingDir.listRecursively
      .filter(!_.isChildOf(workingDir/Constants.SGIT_ROOT))
      .filterNot(_.name == Constants.SGIT_ROOT)
      .filter(!_.name.contains("DS_Store"))//TODO remove
      .filterNot(_.isDirectory)
      .foreach(file => Blob(file, workingDirectory).save())
  }else{
    if((workingDir/param).isRegularFile) Blob(workingDir/param, workingDirectory).save()
    else println(Console.RED + "invalid file name")
  }

  }

  /**
    * Create a new commit containing the current contents of the index
    * and the given log message describing the changes.
    * The new commit is a direct child of HEAD.
    * @param message
    */
  def commit(message : String) : Unit = {
    if (!loadSystem) return

    var outMessage : String = ""

    //Check if staged area is sync with last commit
    if(!isFirstCommit && stage.isStageSync) {
      outMessage = s"On branch ${branch.getCurrentBranch}\nnothing to commit, working tree clean"
      println(outMessage)
      return
    }

    //Get Blobs from Stage
    val stageBlob = stage.getStageBLOBS

    //Create ~tree to commit
    val tree = Tree(head, workingDirectory, stageBlob)

    //Save the tree
    val commitSHATree : File = tree.save()

    //Default if first commit
    var parentCommitSHA = "NONE"
    if (!isFirstCommit) {
      parentCommitSHA = (File(workingDirectory)/Constants.DEFAULT_HEAD_PATH).lines.head
    }

    val commit = Commit(commitSHATree, workingDirectory, parentCommitSHA, message)

    parentCommitSHA = commit.save

    //HEAD points to new commit
    (workingDir/Constants.SGIT_ROOT/"HEAD")
      .clear
      .appendLine("refs/heads/"+branch.getCurrentBranch)

    //Create commit file which head points to
    (workingDir/Constants.SGIT_ROOT/"refs"/"heads"/branch.getCurrentBranch)
      .createIfNotExists()
      .clear
      .appendLine(parentCommitSHA)

    outMessage = s"[${branch.getCurrentBranch} (root-commit) ${parentCommitSHA}] $message\n"+
      s" ${stageBlob.size} files changed, ${stageBlob.size} insertions(+)\n"

    var newFiles : String = ""

    stageBlob.foreach(f =>{
      newFiles = " create "+f._1+"\n"+newFiles
    })

    outMessage = outMessage+newFiles

    println(outMessage)

  }

  /**
    * Displays paths that have differences between the
    * index file and the current HEAD commit, paths that
    * have differences between the working tree and the
    * index file, and paths in the working tree that
    * are not tracked by Sgit
    */
  def status() : Unit = {
    if (!loadSystem) return
    var message: String = ""

    if(isFirstCommit){
      message = s"On branch master\n\nNo commits yet\n\n"
    }else{
      message = s"On branch ${BranchHandler(workingDirectory).getCurrentBranch}\n\n"
    }

    /*
      Check changes to be commited
     */
    if(!stage.isStageSync){
      message = message + "Changes to be commited:\n\n"
      var stagedFileNamesString:String = ""
      stage.getModifiedFilesInStage
        .toSeq.sortBy(_._2).foreach(f => {
        f._2 match {
          case Constants.NEW => stagedFileNamesString += Console.GREEN + "\t\tnew file: " + f._1 + "\n" + Console.RESET
          case Constants.MODIFIED => stagedFileNamesString += Console.GREEN + "\t\tmodified: " + f._1 + "\n" + Console.RESET
        }
      })

      message = message + Console.GREEN + stagedFileNamesString + Console.RESET + "\n"
    }//else{
     // message = message + "nothing to commit, working tree clean"
     // println(message)
      //return
    //}

    /*
      Check untracked and modified files from workingDir files
     */
    //if(!isFirstCommit) {
      var modifiedStage = ""
      if (FileHandler(workingDir).getModifiedFilesFromWorkingDirectory.nonEmpty) {
        message += "Changes not staged for commit:\n\t(use \"sgit add <file>...\" to update what will be committed)\n\n"
        FileHandler(workingDir).getModifiedFilesFromWorkingDirectory.toSeq.sortBy(_._2).reverse.foreach(f => {
          f._2 match {
            case Constants.DELETED => modifiedStage += "\t\tdeleted: " + f._1 + "\n"
            case Constants.MODIFIED => modifiedStage += "\t\tmodified: " + f._1 + "\n"
          }
        })
        message += Console.RED + modifiedStage + Console.RESET + "\n"
      }
    //}

    val newFiles = FileHandler(workingDir).getWorkingDirFiles.diff(stage.getStagedFilesName)

    if(newFiles.nonEmpty){
      message += "Untracked files:\n\t(use \"sgit add <file>...\" to include in what will be committed)\n\n"
      var untrackedFiles: String = ""
      newFiles.foreach(fileName => untrackedFiles = "\t\t"+fileName+"\n"+untrackedFiles)
      message = message + Console.RED + untrackedFiles + Console.RESET
    }

    println(message)

  }

  /**
    * Method to load the sgit basic system information
    * @return
    */
  def loadSystem: Boolean = {
    if(!FileHandler(workingDir).isGitBaseCreated) {
      println(Console.RED + "fatal: not a sgit repository" + Console.RESET)
      return false
    }
    if(!FileHandler(workingDir).hasPermission) {
      println(Console.RED + "fatal: not allowed to initialize sgit" + Console.RESET)
      return false
    }
    head = workingDir/Constants.SGIT_ROOT/"HEAD"

    true
  }

  def diff() : Unit = {
    val lines = FileHandler(workingDir).getDiffLinesWithStaged
    lines.foreach(f => {
      println("Modifications in " + f._1 + " file")
      f._2.foreach(lines => {
        if(lines._2 != "")
        lines._1 match {
          case "added(+)" => printDiff(Console.GREEN, lines)
          case "removed(-)" => printDiff(Console.RED, lines)
        }
      })
      println()
    })
  }

  def printDiff(color : String, lines: (String, String)) : Unit = {
    println(color + "\t"+ lines._1 + "\t" + lines._2 + Console.RESET)
  }

}

object Main extends App{
  val sgit : Sgit = Sgit("")
  if(args.isEmpty) println(Functions.helpMessage)
  else
  args.head match {
      case "init" => sgit.init()
      case "add" if args.tail.isEmpty => println("Nothing specified, nothing added.\nMaybe you wanted to say 'git add .'?")
      case "add" => args.tail.foreach(f => {sgit.add(f)})
      case "status" => sgit.status()
      case "commit" => sgit.commit(args(2))
      case "diff" => sgit.diff()
      case _ => println(Functions.helpMessage)
  }


}

