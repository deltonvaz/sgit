package misc

import better.files.File

case class FileHandler(var workingDirectory : File){

  /**
    * Function to check if sgit base files are already created
    * @return true if base files are already created
    */
  def isGitBaseCreated: Boolean = {
    (workingDirectory/".sgit/").exists
  }

  /**
    * Function used to create git base files
    */
  def createGitBaseFiles(): Unit = {
    (workingDirectory/".sgit").createDirectoryIfNotExists()
    (workingDirectory/".sgit"/"HEAD").createFileIfNotExists()
    (workingDirectory/".sgit"/"INDEX").createFileIfNotExists()
    (workingDirectory/".sgit"/"objects").createDirectoryIfNotExists()
    (workingDirectory/".sgit"/"refs").createDirectoryIfNotExists()
    (workingDirectory/".sgit"/"refs"/"heads").createDirectoryIfNotExists()
    (workingDirectory/".sgit"/"refs"/"tags").createDirectoryIfNotExists()
  }

  /**
    * Check permission of user to create/update/delete/write
    * new files and folders
    * @return true if user has permission to read/write
    */
//  def hasPermission: Boolean = {
//    (workingDirectory/".sgit/").permissionsAsString == "rwxr-xr-x"
//  }

  /**
    * Function to check if the base files of sgit are already created
    */
  def systemVerify(): Boolean = {
    isGitBaseCreated
  }

  /**
    * @deprecated
    * Function that was used to check if files exists or not
    * @param fileName name of the file to check if exists
    * @return
    */
  def fileExists(fileName : String): Boolean = {
    (workingDirectory/fileName).isRegularFile
  }

  /**
    * Get all files that are in staged area
    * @return a map with SHA->fileName
    */
  def getStagedArea : Option[Map[String, String]] = {
    val stageArea = workingDirectory/Constants.SGIT_ROOT/"INDEX"
    if(stageArea.isEmpty) None
    Some(
      stageArea.lines
        .map(_.split(" "))
        .map { case Array(k, v) => (v, k) }//FilePath -> SHA}
        .toMap
    )
  }

  //TODO fix: create a function to get only working dir files
  //TODO make a copy in objects
  /**
    * @return a Set with the filename in the workingSet
    */
  def getWorkingDirFilesRec : Set[String] = {
    var repFiles: Set[String] = Set()
    workingDirectory.listRecursively
      .filter(!_. isChildOf(workingDirectory/Constants.SGIT_ROOT))
      .filter(_.isRegularFile)
      .filter(!_.name.contains("DS_Store"))//TODO remove macOs
      .filter(!_.name.contains("sgit"))
      .filter(!_. isChildOf(workingDirectory/"project")) //TODO remove
      .filter(!_. isChildOf(workingDirectory/"target")) //TODO remove
      .filter(!_. isChildOf(workingDirectory/".git")) //TODO remove
      .foreach(f => {
      repFiles = repFiles.+(f.path.toString diff workingDirectory+"/")
    })
    repFiles
  }

  /**
    * Function that returns a map with fileName -> fileStatus
    * where fileStatus could be deleted, modified or new
    * @return a map[fileName, {deleted, modified, new}]
    */
  def getModifiedFilesFromWorkingDirectory(shaValue : Boolean) : Map[String, Int] = {
    var files : Map[String, Int] = Map()
    val stage = StageHandler(workingDirectory.path.toString)
    if(!shaValue){
      //Get deleted
      stage
        .getStagedFilesNameRec.diff(getWorkingDirFilesRec)
        .foreach(f => {
          files += (f -> Constants.DELETED)
        })
      //get modified
      var shaWorkingDir : String = ""
      getWorkingDirFilesRec
        .foreach(fileName => {
          shaWorkingDir = (workingDirectory/fileName).sha1
          if(!stage.getStageBLOBS.exists(_ == (fileName -> shaWorkingDir)) && stage.getStageBLOBS.contains(fileName)) {
            files += (fileName -> Constants.MODIFIED)
          }
        })
    }else {
      var shaWorkingDir : String = ""
      getWorkingDirFilesRec
        .foreach(fileName => {
          shaWorkingDir = (workingDirectory/fileName).sha1
          if(!stage.getStageBLOBS.exists(_ == (fileName -> shaWorkingDir)) && stage.getStageBLOBS.contains(fileName)) {
            files += (shaWorkingDir -> Constants.MODIFIED)
          }
        })
    }
    files
  }

  /**
    *
    * @return A map[fileName, IndexedSeq[(removedOrAdded, lines)]
    */
  def getDiffLinesWithStaged : Map[String, IndexedSeq[(String, String)]]  = { //old -> new
    var stagedLines : IndexedSeq[String] = IndexedSeq()
    var workingLines : IndexedSeq[String] = IndexedSeq()
    var retVal : Map[String, IndexedSeq[(String, String)]] = Map()
    getModifiedFilesFromWorkingDirectory(false)
      .filter(_._2 == Constants.MODIFIED)
      .foreach(file => {
        val fileSha = StageHandler(workingDirectory.path.toString).getContentFromName(file._1)
        stagedLines = (workingDirectory/Constants.OBJECTS_FOLDER/fileSha.get).lines.toIndexedSeq
        workingLines = (workingDirectory/file._1).lines.toIndexedSeq
        val removed = (stagedLines diff workingLines).map(line => ("removed(-)", line))
        val added = (workingLines diff stagedLines).map(line => ("added(+)", line))
        val mappedLines : IndexedSeq[(String, String)] = removed++added.sortBy(_._2).toIndexedSeq
        retVal = retVal.+(file._1 -> mappedLines)
      })
    retVal
  }

  /**
    *
    * @return A map[fileName, IndexedSeq[(removedOrAdded, lines)]
    */
  def getDiffLinesWithParent(commitSha : String) : Map[String, IndexedSeq[(String, String)]]  = { //old -> new
    val commit = CommitHandler(workingDirectory.path.toString)
    commit.compareCommitFilesWithParent(commitSha)

//    //parent commit lines
//    var stagedLines : IndexedSeq[String] = IndexedSeq()
//    //current commit lines
//    var workingLines : IndexedSeq[String] = IndexedSeq()
//    //returned values with [docName, Seq[line, added/removed]]
//    var retVal : Map[String, IndexedSeq[(String, String)]] = Map()
//
//    var modifiedFiles : Map[String, Int] = Map()
//    println("commitSHA "+ commitSha)
//
//    modifiedFiles = getModifiedFilesFromCommit(commitSha)

//    modifiedFiles
//      .filter(_._2 == Constants.MODIFIED)
//      .foreach(file => {
//        val currentCommitLines = StageHandler(workingDirectory.path.toString).getObjectContent(file._1)
//        val parentCommitLines = StageHandler(workingDirectory.path.toString).getObjectContent(file._2)
//        println(file._1)
//        println(file._2)
////        val fileSha = StageHandler(workingDirectory.path.toString).getContentFromName(file._1)
////        //parent
////        stagedLines = (workingDirectory/Constants.OBJECTS_FOLDER/fileSha.get).lines.toIndexedSeq
////        //current
////        workingLines = (workingDirectory/file._1).lines.toIndexedSeq
////
//////        println(stagedLines)
//////        println(workingLines)
////
////        val removed = (stagedLines diff workingLines).map(line => ("removed(-)", line))
////        val added = (workingLines diff stagedLines).map(line => ("added(+)", line))
////        val mappedLines : IndexedSeq[(String, String)] = removed++added.sortBy(_._2).toIndexedSeq
////        retVal = retVal.+(file._1 -> mappedLines)
//      })
//    retVal
  }

}
