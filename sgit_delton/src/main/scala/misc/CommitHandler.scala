package misc

import better.files.File

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

      (File(workingDir)/Constants.OBJECTS_FOLDER/getTreeFromCommit(lastCommit)).lines.foreach {
        case Constants.STAGE_PATTERN(id, fileName) => {
          retVal = retVal + (fileName -> id) //fileName unique
        }
        case _ => println(Console.RED + "Error reading tree files" + Console.RESET)
      }

      retVal
    }
  }

  /**
    * Function to get the tree from given commit
    * @param commitHeader header of commit in (tree parent date comment) pattern
    * @return SHA1 that commitHeader points to
    */
  def getTreeFromCommit(commitHeader : String) : String = {
    val lastCommitTree = (File(workingDir)/Constants.OBJECTS_FOLDER/commitHeader).lines.head
    var commitTreeSHA = ""
    lastCommitTree match {
        case Constants.COMMIT_PATTERN(treeSHA, parentSha, date, comment) => {
        commitTreeSHA = treeSHA
      }
        case _ => println(Console.RED + "Error reading commited files" + Console.RESET)
    }
    commitTreeSHA
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
    * Used to get last commit fileName(s)
    * @return Set[lastCommitFileName(s)]
    */
  def getLastCommitFiles : Set[String] = {
    if (isFirstCommit) return Set()
    val currentBranch = (File(workingDir)/Constants.SGIT_ROOT/"HEAD").lines.head

    val lastCommit = (File(workingDir)/Constants.SGIT_ROOT/currentBranch).lines.head

    var commitFiles: Set[String] = Set()
    (File(workingDir) / Constants.OBJECTS_FOLDER / getTreeFromCommit(lastCommit)).lines.foreach {
      case Constants.STAGE_PATTERN(sha1, fileName) => {
        commitFiles = commitFiles.+(fileName)
      }
    }
    commitFiles
  }

}
