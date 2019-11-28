package misc

import better.files.File
import objects.Blob

import scala.io.AnsiColor
import scala.util.Random

object Functions {
  /**
    * Diff printer beautify, should be a general beautify
    * @param color color used for message
    * @param lines lines that should be printed with colors
    */
  def printDiff(color : String, lines: (String, String)) : Unit = {
    println(color + "\t"+ lines._1 + "\t" + lines._2 + Console.RESET)
  }


  def stringFormatter(str: String, color: String): String = {
    color + str + Console.RESET
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
      case Seq(".") => {
      workingDir.listRecursively
      .filter(!_.isChildOf(workingDir/Constants.SGIT_ROOT))
      .filterNot(_.name == Constants.SGIT_ROOT)
      .filter(!_.name.contains("DS_Store"))//TODO remove
      .filter(!_.name.equals("sgit"))
      .filterNot(_.isDirectory)
      .foreach(file => Blob(file, workingDir.pathAsString).save())
        ("", true)
      }
      case _ => {
        var retString = ""
        var retBool = true
        fileNames.foreach(fileName => {
          if ((workingDir/fileName).notExists) {
            retString = s"$fileName " + Constants.MSG_NOT_FOUND
            retBool = false
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
