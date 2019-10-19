package misc

import better.files.File

import scala.util.matching.Regex

case class StageHandler(workingDir : String) {

  val workDir = File(workingDir)
  val stageArea: File = workDir/Constants.SGIT_ROOT/"INDEX"

  def getStagedFileLines : Traversable[String] = stageArea.lines

  def getStagedFilesName : Set[String] = {
    val staged = (workDir/Constants.SGIT_ROOT/"INDEX").lines
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

  def getStagedFiles : Set[File] = {
    val staged = (workDir/Constants.SGIT_ROOT/"INDEX").lines
    var stagedFiles: Set[File] = Set()
    staged.foreach {
      case Constants.STAGE_PATTERN(sha1, fileName) =>
        stagedFiles = stagedFiles.+(File(fileName))
    }
    stagedFiles
  }

  /**
    * Function that check if stage area is sync with last commit
    * @return true if the stage area is sync with last commit
    */
  def isStageSync : Boolean = {
    CommitHandler(workingDir).getLastCommitBLOBS == getStageBLOBS
  }

  /**
    * Function to get blobs from index
    * @return map[fileName, SHA]
    */
  def getStageBLOBS : Map[String, String] = {
    var blobSHAs = Map[String, String]()
    getStagedFileLines.foreach {
      case Constants.STAGE_PATTERN(id, fileName) => {
        blobSHAs = blobSHAs + (fileName -> id) //fileName unique
      }
      case _ => println(Console.RED + "Error reading staged files" + Console.RESET)
    }
    blobSHAs
  }

  def getModifiedFilesInStage : Map[String, Int] = {
    val stagedFiles : Map[String, String] = StageHandler(workingDir).getStageBLOBS
    val commitedFiles : Map[String, String] = CommitHandler(workingDir).getLastCommitBLOBS
    var modifiedFilesInStage : Map [String, Int] = Map()
    stagedFiles.foreach(file => {
      //new files
      if(!commitedFiles.contains(file._1)){
        modifiedFilesInStage += (file._1 -> Constants.NEW)
      }
      //modified files
      if(commitedFiles.contains(file._1) && commitedFiles.get(file._1).mkString("") != file._2){
        modifiedFilesInStage += (file._1 -> Constants.MODIFIED)
      }
      //Renamed files
    })

    modifiedFilesInStage
  }

  def getContentFromName(fileName : String) : Option[String] = {
    getStageBLOBS.get(fileName)
  }

}
