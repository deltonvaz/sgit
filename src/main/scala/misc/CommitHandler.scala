package misc

import better.files.File

import scala.annotation.tailrec

case class CommitHandler (workingDir : String) {

  /**
    * Check stage area and HEAD to check if is the first commit or not
    * @return
    */
  def isFirstCommit : Boolean = {
    (File(workingDir)/Constants.SGIT_ROOT/"HEAD").isEmpty
  }

  /**
    * Return last commit map(FileName -> SHA)
    * @return
    */
  def getLastCommitBLOBS : Map[String, String] = {
    var retVal : Map[String, String] = Map()
    if (isFirstCommit) {
      retVal
    }
    else {
      val currentBranch = (File(workingDir)/Constants.SGIT_ROOT/"HEAD").lines.head

      val lastCommit = (File(workingDir)/Constants.SGIT_ROOT/currentBranch).lines.head

      (File(workingDir)/Constants.OBJECTS_FOLDER/getTreeFromCommitSHA(lastCommit, parent = false)).lines.foreach(str => {
        retVal = retVal + (str.split(" ")(1) -> str.split(" ")(0))
      })
//      {
//        case Constants.STAGE_PATTERN(id, fileName) => {
//          retVal = retVal + (fileName -> id) //fileName unique
//        }
//        case _ => println(Console.RED + "Error reading tree files" + Console.RESET)
//      }

      retVal
    }
  }

  /**
    * Function to get the tree file (with SHA1) from given commit
    * @param commitSHA header of commit in (tree parent date comment) pattern
    * @param parent boolean to get commit's parent files
    * @return SHA1 that commitHeader points to
    */
  def getTreeFromCommitSHA(commitSHA : String, parent : Boolean) : String = {
    val lastCommitTree = (File(workingDir)/Constants.OBJECTS_FOLDER/commitSHA).lines.head
    var commitTreeSHA = ""
    lastCommitTree match {
        case Constants.COMMIT_PATTERN(treeSHA, parentSha, date, comment, user) => {
          if (parent){
            commitTreeSHA = parentSha
          }else{
            commitTreeSHA = treeSHA
          }
      }
        case _ => println(Console.RED + "Error reading commited files" + Console.RESET)
    }
    commitTreeSHA
  }

  def getCommitNameFromSHA(commitSHA : String) : String = {
    val lastCommitTree = (File(workingDir)/Constants.OBJECTS_FOLDER/commitSHA).lines.head
    lastCommitTree match {
      case Constants.COMMIT_PATTERN(treeSHA, parentSha, date, comment, user) => {
        comment
      }
      case _ =>
        println(Console.RED + "Error reading commited files" + Console.RESET)
        ""
    }

  }

  /**
    * deprecated
    * Used to generate first version delton_sgit with trees
    * @param path
    * @return
    */
  def isRootFile(path : String) : Boolean = {
    (File(workingDir)/path).parent.path.toString == workingDir
  }

  /**
    * get last commit fileName(s)
    * @return Set[lastCommitFileName(s)]
    */
  def getLastCommitFiles : Set[String] = {
    if (isFirstCommit) return Set()
    val currentBranch = (File(workingDir)/Constants.SGIT_ROOT/"HEAD").lines.head

    val lastCommit = (File(workingDir)/Constants.SGIT_ROOT/currentBranch).lines.head

    var commitFiles: Set[String] = Set()
    (File(workingDir) / Constants.OBJECTS_FOLDER / getTreeFromCommitSHA(lastCommit, parent = false)).lines.foreach {
      case Constants.STAGE_PATTERN(sha1, fileName) => {
        commitFiles = commitFiles.+(fileName)
      }
    }
    commitFiles
  }

  /**
    * show all commits starting with the newest
    */
  def getCommitsHistoric(historic : Boolean): Boolean =  {
    if (isFirstCommit) {
      println(Constants.MSG_FATAL_NO_COMMITS)
      false
    }
    else{
      val currentBranch = (File(workingDir)/Constants.SGIT_ROOT/"HEAD").lines.head
      val lastCommitSHA = (File(workingDir)/Constants.SGIT_ROOT/currentBranch).lines.head
      commitsRecursion(lastCommitSHA, historic)
      true
    }
  }

  /***
    * Check last commits with diff optional param
    * @param commitSha - commit's sha
    */
  @tailrec
  final def commitsRecursion(commitSha:String, historic : Boolean): Unit ={
    if(commitSha == "NONE") return
      val lastCommitTree = (File(workingDir)/Constants.OBJECTS_FOLDER/commitSha).lines.head
      lastCommitTree match {
        case Constants.COMMIT_PATTERN(treeSHA, parentSha, date, comment, user) => {
          var msg = Functions.stringFormatter("Commit: "+ treeSHA, Console.YELLOW) + "\n"
          msg = msg + "Author: "+user + "\n"
          msg = msg + "Date: "+date + "\n\n"
          msg = msg + "\t\t"+comment + "\n\n"
          println(msg)
          if(historic){
            val lines = FileHandler(File(workingDir)).getDiffLinesWithParent(commitSha)
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

          commitsRecursion(parentSha, historic)
        }
        case _ => println(Console.RED + "Error reading commited files" + Console.RESET)
      }
  }

  /**
    * Compare differences between a commit and its parent
    * @param commitSha
    * @return
    */
  def compareCommitFilesWithParent(commitSha : String) : Map[String, IndexedSeq[(String, String)]] = {
    val commitTree = getTreeFromCommitSHA(commitSha, parent = false)
    val parentCommitTree = getTreeFromCommitSHA(commitSha, parent = true)

    var retVal : Map[String, IndexedSeq[(String, String)]] = Map()

    if(parentCommitTree !=  "NONE"){
      val parentCommitFiles = getTreeFromCommitSHA(parentCommitTree, parent = false)
      val currentCommit = (File(workingDir)/Constants.OBJECTS_FOLDER/commitTree).lines.toIndexedSeq
      val parentCommit = (File(workingDir)/Constants.OBJECTS_FOLDER/parentCommitFiles).lines.toIndexedSeq

      var currentCommitBlobSHAs = Map[String, String]()
      currentCommit.foreach(file => {
        currentCommitBlobSHAs = currentCommitBlobSHAs + (file.split(" ")(1) -> file.split(" ")(0))
      })

      var parentCommitBlobSHAs = Map[String, String]()
      parentCommit.foreach(file => {
        parentCommitBlobSHAs = parentCommitBlobSHAs + (file.split(" ")(1) -> file.split(" ")(0))
      })

      //Check modified files
      currentCommitBlobSHAs
        .foreach(fileName => {
          if(!parentCommitBlobSHAs.exists(_ == (fileName._2 -> fileName._1)) && parentCommitBlobSHAs.contains(fileName._1)) {
            val parentSHA = parentCommitBlobSHAs.get(fileName._1)
            val parentLines = (File(workingDir)/Constants.OBJECTS_FOLDER/parentSHA.get).lines.toIndexedSeq
            val childSHA = fileName._2
            val childLines = (File(workingDir)/Constants.OBJECTS_FOLDER/childSHA).lines.toIndexedSeq

            val removed = (parentLines diff childLines).map(line => ("removed(-)", line))
            val added = (childLines diff parentLines).map(line => ("added(+)", line))
            val mappedLines : IndexedSeq[(String, String)] = removed++added.sortBy(_._2).toIndexedSeq
            retVal = retVal.+(fileName._1 -> mappedLines)
          }
        })

    }

    retVal

  }


}
