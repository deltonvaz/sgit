package functions

import better.files.File

case class StageHandler(workingDir : String) {

  val workDir = File(workingDir)
  val stageArea: File = workDir/Constants.SGIT_ROOT/"INDEX"

  def getStagedFileLines : Traversable[String] = stageArea.lines

  /**
    *
    * @return a recursively set[fileNames] of the working dir
    */
  def getStagedFilesNameRec : Set[String] = {
    val staged = (workDir/Constants.SGIT_ROOT/"INDEX").lines
    var stagedFiles: Set[String] = Set()
    staged.foreach(file => {
      stagedFiles = stagedFiles.+(file.split(" ")(1))
    })
    stagedFiles
  }

  /**
    *
    * @return a set[fileNames] of the working dir
    */
  def getStagedFilesName : Set[String] = {
    val staged = (workDir/Constants.SGIT_ROOT/"INDEX").lines
    var stagedFiles: Set[String] = Set()

    staged.foreach(file => {
      stagedFiles = stagedFiles.+(file.split(" ")(1))
    })

    stagedFiles
  }

  /**
    *
    * @return a Set[StagedFiles] with its content
    */
  def getStagedFiles : Set[File] = {
    val staged = (workDir/Constants.SGIT_ROOT/"INDEX").lines
    var stagedFiles: Set[File] = Set()
    staged.foreach(file => {
      stagedFiles = stagedFiles.+(File(file.split(" ")(1)))
    })
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
    getStagedFileLines.foreach(str => {
      blobSHAs = blobSHAs + (str.split(" ")(1) -> str.split(" ")(0))
    })
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

  def getObjectContent(objectSHA : String) : Option[File] = {
    Some(workDir/Constants.OBJECTS_FOLDER/objectSHA)
  }

  /**
    * Remove file object from staged area
    * @param objectName
    * @return
    */
  def removeStagged(objectName : String) : Boolean = {
    var stagedFiles : Map[String, String] = FileHandler(workDir).getStagedArea.get
    if(stagedFiles.contains(objectName)){
      stagedFiles = stagedFiles.-(objectName)
      val stagedArea = (workDir/Constants.SGIT_INDEX).clear()
      stagedFiles.foreach(str => {
        stagedArea.appendLine(str._2 + " " + str._1)
      })
      true
    }else{
      false
    }
  }

}
