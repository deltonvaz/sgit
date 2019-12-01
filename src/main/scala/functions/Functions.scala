package functions

import better.files.File
import objects.{Blob, Commit, Tree}

import scala.util.Random

object Functions {
  /**
    * Diff printer beautify
    * @param color color used for message
    * @param lines lines that should be printed with colors
    */
  def printDiff(color : String, lines: (String, String)) : Unit = {
    println(color + "\t"+ lines._1 + "\t" + lines._2 + Console.RESET)
  }

  /**
    * String format printer beautify, should be a general beautify
    * @param color color used for message
    * @param str lines that should be printed with colors
    */
  def stringFormatter(str: String, color: String): String = {
    color + str + Console.RESET
  }

  /**
    * Error beautify printer
    * @param str
    */
  def printError(str : String) : Unit = {
    println(Console.RED + str + Console.RESET)
  }

  /**
    *
    * @param str
    * @return
    */
  def randomStringFormatter(str: String) : String = {
    val possibleColors = Seq (
      "\u001b[30m",
      "\u001b[32m",
      "\u001b[33m",
      "\u001b[34m",
      "\u001b[35m",
      "\u001b[36m",
      "\u001b[37m"
    )
    val rand = new Random(System.currentTimeMillis())
    val random_index = rand.nextInt(possibleColors.length)
    possibleColors(random_index) + str + Console.RESET

  }

  /**
    * add main method
    * @param workingDir main working directory
    * @param fileNames files that are being added
    * @return
    */
  def add(workingDir : File, fileNames : Seq[String]) : (String, Boolean) = {
    fileNames match {
      case Seq() => ("no file selected", false)
      case _ => {
        var retString = ""
        var retBool = true
        fileNames.foreach(fileName => {
          if(fileName == "."){
            workingDir.listRecursively
              .filter(!_.isChildOf(workingDir/Constants.SGIT_ROOT))
              .filterNot(_.name == Constants.SGIT_ROOT)
              //.filter(!_.name.contains("DS_Store"))//TODO remove
              .filter(!_.name.equals("sgit"))
              .filterNot(_.isDirectory)
              .foreach(file => Blob(file, workingDir.pathAsString).save())
            retString = ""
            retBool = true
          }
          else if ((workingDir/fileName).notExists) {
            //If the file is not found I need to check if it is in Stage area and then remove it
            if(StageHandler(workingDir.pathAsString).removeStagged(fileName)){
              retString = ""
              retBool = true
            }else{
              retString = s"$fileName " + Constants.MSG_NOT_FOUND
              retBool = false
            }
          }
          else if ((workingDir/fileName).isRegularFile) {
            Blob(workingDir/fileName, workingDir.pathAsString).save()
            retString = ""
            retBool = true
          }
          else {
            retString = Constants.MSG_INVALID_FILE
            retBool = false
          }
        })
        (retString, retBool)
      }
    }
  }

  /**
    * status main method
    * @param workingDir current working directory
    * @return
    */
  def status(workingDir : File) : (String, Map[String, Boolean]) = {
    var message: String = ""
    var params : Map[String, Boolean] = Map()
    val stage = StageHandler(workingDir.pathAsString)

    if(CommitHandler(workingDir.pathAsString).isFirstCommit){
      message = s"On branch master\n\nNo commits yet\n\n"
      params = params.+("firstCommit" -> true)
    }else{
      message = s"On branch ${BranchHandler(workingDir.pathAsString).getCurrentBranch}\n\n"
      params = params.+("firstCommit" -> false)
    }

    /*
      Check changes to be commited
     */
    params = params.+("changes" -> false)
    if(!stage.isStageSync){
      params = params.+("changes" -> true)
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
      Modified/deleted files from workingDir
     */
    params = params.+("modified" -> false)
    var modifiedStage = ""
    if (FileHandler(workingDir).getModifiedFilesFromWorkingDirectory(false).nonEmpty) {
      params = params.+("modified" -> true)
      message += "Changes not staged for commit:\n\t(use \"sgit add <file>...\" to update what will be committed)\n\n"
      FileHandler(workingDir).getModifiedFilesFromWorkingDirectory(false).toSeq.sortBy(_._2).reverse.foreach(f => {
        f._2 match {
          case Constants.DELETED => modifiedStage += "\t\tdeleted: " + f._1 + "\n"
          case Constants.MODIFIED => modifiedStage += "\t\tmodified: " + f._1 + "\n"
        }
      })
      message += Console.RED + modifiedStage + Console.RESET + "\n"
    }

    val newFiles = FileHandler(workingDir).getWorkingDirFilesRec.diff(stage.getStagedFilesNameRec)

    /*
      Untracked files
     */
    params = params.+("untracked" -> false)
    if(newFiles.nonEmpty){
      params = params.+("untracked" -> true)
      message += "Untracked files:\n\t(use \"sgit add <file>...\" to include in what will be committed)\n\n"
      var untrackedFiles: String = ""
      newFiles.foreach(fileName => untrackedFiles = "\t\t"+fileName+"\n"+untrackedFiles)
      message = message + Console.RED + untrackedFiles + Console.RESET
    }

    (message, params)
  }

  def commit(workingDir : File, message : String) : (String, Map[String, Boolean]) = {
    var outMessage : String = ""
    var params : Map[String, Boolean] = Map()
    //Stage handler
    val stage = StageHandler(workingDir.pathAsString)

    //branch handler
    val branch : BranchHandler = BranchHandler(workingDir.pathAsString)

    //head file
    val head = workingDir/Constants.SGIT_ROOT/"HEAD"

    //commit handler
    val commitHandler = CommitHandler(workingDir.pathAsString)

    params = params.+("firstCommit" -> true)
    params = params.+("sync" -> true)

    //output message
    var whichCommit = "(root-commit)"
    //Check if staged area is sync with last commit
    if(!commitHandler.isFirstCommit && stage.isStageSync) {
      whichCommit = ""
      outMessage = s"On branch ${branch.getCurrentBranch}\nnothing to commit, working tree clean"
      params = params.+("firstCommit" -> false)
      params = params.+("sync" -> true)
      return (outMessage, params)
    }

    //Get Blobs from Stage
    val stageBlob = stage.getStageBLOBS


    //Create tree to commit
    val tree = Tree(head, workingDir.pathAsString, stageBlob)

    //Save the tree
    val commitSHATree : File = tree.save()

    //default parent commit if it is first commit
    var parentCommitSHA = "NONE"

    //If is not first commit
    if (!commitHandler.isFirstCommit) {
      params = params.+("firstCommit" -> false)
      params = params.+("sync" -> false)
      whichCommit = ""
      parentCommitSHA = (workingDir/Constants.DEFAULT_HEAD_PATH).lines.head
    }

    val commit = Commit(commitSHATree, workingDir.pathAsString, parentCommitSHA, message)

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

    outMessage = s"[${branch.getCurrentBranch} $whichCommit $parentCommitSHA] $message\n"+
      s" ${stageBlob.size} file(s) commited\n"

    var newFiles : String = ""

    stageBlob.foreach(f =>{
      newFiles = " file name: "+f._1+"\n"+newFiles
    })

    outMessage = outMessage+newFiles

    (outMessage, params)
  }

}
