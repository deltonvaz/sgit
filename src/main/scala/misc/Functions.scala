package misc

import better.files.File
import objects.Blob

import scala.util.Random

object Functions {


  def printDiff(color : String, lines: (String, String)) : Unit = {
    println(color + "\t"+ lines._1 + "\t" + lines._2 + Console.RESET)
  }

  /**
    * Diff printer beautify, should be a general beautify
    * @param color color used for message
    * @param str lines that should be printed with colors
    */
  def stringFormatter(str: String, color: String): String = {
    color + str + Console.RESET
  }

  def printError(str : String) : Unit = {
    println(Console.RED + str + Console.RESET)
  }

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
              .filter(!_.name.contains("DS_Store"))//TODO remove
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


  val helpMessage : String = """
  Usage:
    Create an empty Sgit repository
    """+ Console.GREEN + "init" + Console.RESET +
  """

  Show the working tree status
    """ + Console.GREEN + "status" + Console.RESET +
  """

  Show changes between commits, commit and working tree, etc
    """ + Console.GREEN + "diff" + Console.RESET +
  """

  Add file contents to the index
    """ + Console.GREEN + "add <filename/filenames or . or regexp>" + Console.RESET +
    """

  Record changes to the repository
    """ + Console.GREEN + "commit <filename/filenames or . or regexp>" + Console.RESET +
  """

  Show commit logs
    """ + Console.GREEN + "log" + Console.RESET +
  """

  Show commit logs started with newest
    """ + Console.GREEN + "log -p" + Console.RESET +
  """

  Show changes overtime
    """ + Console.GREEN + "log -stat" + Console.RESET +
  """

  Create a new branch
    """ + Console.GREEN + "branch <branch name>" + Console.RESET +
  """

  List all existing branches and tags
    """ + Console.GREEN + "branch -av" + Console.RESET +
    """

  Switch branches or restore working tree files
    """ + Console.GREEN + "checkout <branch or tag or commit hash>" + Console.RESET +
  """

  Create tag
    """ + Console.GREEN + "tag <tag name>" + Console.RESET +
    """

  Join two or more development histories together
    """ + Console.GREEN + "merge <branch>" + Console.RESET +
  """

  Reapply commits on top of another base tip
    """ + Console.GREEN + "rebase <branch>" + Console.RESET +
    """

  Make a list of the commits which are about to be rebased. Let the user edit that list before rebasing.
    """ + Console.GREEN + "merge <branch>" + Console.RESET
}
