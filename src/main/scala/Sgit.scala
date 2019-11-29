import better.files.File
import better.files.Dsl.{cwd, mkdirs}
import misc.{BranchHandler, CommitHandler, Config, Constants, FileHandler, Functions, StageHandler, TagHandler}
import objects.{Blob, Commit, Tree}
import scopt.OParser

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
    if(FileHandler(workingDir).systemVerify()){
      println(s"Reinitialized existing Sgit repository in $workingDirectory/.sgit")
    }else{
      FileHandler(workingDir).createGitBaseFiles()
      println(s"Initialized empty Sgit repository in $workingDirectory")
    }
  }

  /**
    *
    * function responsible for adding files in the staging area
    *
    * @param param filename(s) / . / regex
    */
  def add(param : Seq[String]): Unit = {
    val message = Functions.add(workingDir, param)._1
    val msgType = Functions.add(workingDir, param)._2
    if (msgType) {
      Functions.stringFormatter(message, Console.GREEN)
    } else {
      Functions.stringFormatter(message, Console.RED)
    }
  }

  /**
    * Create a new commit containing the current contents of the index
    * and the given log message describing the changes.
    * The new commit is a direct child of HEAD.
    * @param message commits name
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

    //TODO change parent commit to current commit and files added
    outMessage = s"[${branch.getCurrentBranch} (root-commit) $parentCommitSHA] $message\n"+
      s" ${stageBlob.size} files changed, ${stageBlob.size} insertions(+)\n"

    var newFiles : String = ""

    //TODO check this create
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
    }

    /*
      Check untracked and modified files from workingDir files
     */
    //if(!isFirstCommit) {
      var modifiedStage = ""
      if (FileHandler(workingDir).getModifiedFilesFromWorkingDirectory(false).nonEmpty) {
        message += "Changes not staged for commit:\n\t(use \"sgit add <file>...\" to update what will be committed)\n\n"
        FileHandler(workingDir).getModifiedFilesFromWorkingDirectory(false).toSeq.sortBy(_._2).reverse.foreach(f => {
          f._2 match {
            case Constants.DELETED => modifiedStage += "\t\tdeleted: " + f._1 + "\n"
            case Constants.MODIFIED => modifiedStage += "\t\tmodified: " + f._1 + "\n"
          }
        })
        message += Console.RED + modifiedStage + Console.RESET + "\n"
      }
    //}

    val newFiles = FileHandler(workingDir).getWorkingDirFilesRec.diff(stage.getStagedFilesNameRec)

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
//    if(!FileHandler(workingDir).hasPermission) {
//      println(Console.RED + "fatal: not allowed to initialize sgit" + Console.RESET)
//      return false
//    }
    head = workingDir/Constants.SGIT_ROOT/"HEAD"

    true
  }

  /**
    * Show changes between working directory and staged area
    */
  def diff() : Unit = {
    if(!loadSystem) return
    val lines = FileHandler(workingDir).getDiffLinesWithStaged
    lines.foreach(f => {
      println("Modifications in " + f._1 + " file")
      f._2.foreach(lines => {
        if(lines._2 != "")
        lines._1 match {
          case "added(+)" => Functions.printDiff(Console.GREEN, lines)
          case "removed(-)" => Functions.printDiff(Console.RED, lines)
        }
      })
      println()
    })
  }

  /**
    * Create a new branch with branchName
    * @param branchCommand name of the new branch
    */
  def branch(branchCommand : String, all : Boolean, verbose : Boolean) : Unit = {
    if(!isFirstCommit) {
      if(all){
        println("Branches\n")
        println(branch.getBranches)
        if(!(TagHandler(workingDirectory).getTags == "")){
          println("\nTags\n")
          println(TagHandler(workingDirectory).getTags)
        }
      }else{
        println(branch.newBranch(branchCommand))
      }
//      branchCommand match {
//        case "-av" => {
//          println("Branches\n")
//          println(branch.getBranches)
//          if(!(TagHandler(workingDirectory).getTags == "")){
//            println("\nTags\n")
//            println(TagHandler(workingDirectory).getTags)
//          }
//        }
//        case _ if branchCommand.equals("") => println("invalid command")
//        case _ => println(branch.newBranch(branchCommand))
//      }
    }else{
      println("fatal: Not a valid object name: 'master'.") //TODO remove
    }
  }

  def tag(tagName : String) : Unit = {
    if(!isFirstCommit) {
      tagName match {
        case _ if tagName.equals("") => println("invalid command")
        case _ => TagHandler(workingDirectory).newTag(tagName)
      }
    }else{
      println("fatal: Not a valid object name: 'master'.") //TODO remove
    }
  }

  /**
    *
    * @param changes
    */
  def log(changes : Boolean, stat : Boolean) : Unit = {
    if(changes){
      println("Show changes overtime")
    }else if(stat){
      println("Show stats about changes overtime")
    }
    CommitHandler(workingDirectory).getCommitsHistoric(true)

    //mostra o commit

    //se changes = true mostra o diff do commit em relacao ao head

  }

}

object Main extends App{
  import scopt.OParser

  val sgit : Sgit = Sgit(cwd.path.toString)

  val builder = OParser.builder[Config]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("delton sgit"),
      head(Functions.randomStringFormatter("delton sgit"), Functions.randomStringFormatter("0.0.2")),
      help("help").text("prints this usage text"),
      cmd("init")
        .action((_, c) => c.copy(mode = "init"))
        .text(Functions.stringFormatter("\tCreate an empty Sgit repository", Console.GREEN)),
      cmd("status")
      .action((_, c) => c.copy(mode = "status"))
        .text(Functions.stringFormatter("\tShow the working tree status", Console.GREEN)),
      cmd("diff")
        .action((_, c) => c.copy(mode = "diff"))
        .text(Functions.stringFormatter("\tShow changes between staged area and working tree", Console.GREEN)),
      cmd("add")
        .action((_, c) => c.copy(mode = "add"))
        .text(Functions.stringFormatter("\tAdd file contents to the index", Console.GREEN))
        .children(
          arg[String]("<filename/filenames or . >")
            .unbounded()
            .required()
            .action((x, c) => c.copy(files = c.files :+ x))
        ),
      cmd("commit")
        .action((_, c) => c.copy(mode = "commit"))
        .text(Functions.stringFormatter("\tRecord changes to the repository", Console.GREEN))
        .children(
          opt[String]('m', "message")
            .text("Commit's message")
            .optional()
            .action((message, c) => c.copy(cmessage = message))
        ),
      cmd("branch")
        .action((_, c) => c.copy(mode = "branch"))
        .text(Functions.stringFormatter("\tTo create new branch", Console.GREEN))
        .children(
          arg[String]("name")
            .text("name of the branch")
            .action((message, c) => c.copy(branchName = message)),
          opt[Unit]('a', "all")
            .action((_, c) => c.copy(showAll = true))
            .text("display all branches created"),
          opt[Unit]('v', "verbose")
            .action((_, c) => c.copy(verbose = true))
            .text("show details branch's commit"),
        ),
      cmd("tag")
        .action((_, c) => c.copy(mode = "tag"))
        .text(Functions.stringFormatter("\tCreate tag", Console.GREEN))
        .children(
          arg[String]("<tag name>")
            .text("Create a new tag")
            .action((message, c) => c.copy(tagName = message)),
        ),
      cmd("log")
        .action((_, c) => c.copy(mode = "log"))
        .text(Functions.stringFormatter("\tTo see historic differences", Console.GREEN))
        .children(
          opt[Unit]('p', "p")
            .action((_, c) => c.copy(plog = true))
            .optional()
            .text("display all branches created"),
          opt[Unit]('s', "status")
            .action((_, c) => c.copy(statLog = true))
            .optional()
            .text("show details branch's commit"),
        ),
    )
  }

  OParser.parse(parser1, args, Config()) match {
    case Some(config) =>
      config.mode match {
        case "init" => sgit.init()
        case "status" => sgit.status()
        case "add" => sgit.add(config.files)
        case "commit" => sgit.commit(config.cmessage)
        case "diff" => sgit.diff()
        case "branch" => sgit.branch(config.branchName, config.showAll, config.verbose)
        case "tag" => sgit.tag(config.tagName)
        case "log" => sgit.log(config.plog, config.statLog)
      }
    case _ =>
    // arguments are bad, error message will have been displayed
  }

}

