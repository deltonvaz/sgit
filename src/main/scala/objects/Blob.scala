package objects

import better.files.File
import functions.Constants

final case class Blob(override val id : File, override val workingDir: String) extends Object {

  final override def objectType = ObjectType.Blob

  val workDir = File(workingDir)

  val stageArea: File = workDir/Constants.SGIT_ROOT/"INDEX"

  def save() : Unit = {
    if(!(workDir/Constants.OBJECTS_FOLDER/id.sha1).exists){
      id.copyToDirectory(workDir/Constants.OBJECTS_FOLDER)
        .renameTo(id.sha1)
    }
    addToStageArea()
  }

  def addToStageArea() : Unit = {
    var stagedLines : Map[String, String] = Map()
    val pathFile : String = id.path.toString diff (workingDir+"/")

    stageArea.lines.foreach(file => {
      val sha1 = file.split(" ")(0)
      val fileName = file.split(" ")(1)
      stagedLines = stagedLines.+(fileName -> sha1)
    })

    //Check if SHA is already registered into staged area
    stagedLines = stagedLines.+(pathFile -> id.sha1)

    stageArea.clear()
    stagedLines.foreach(f => {
      stageArea.appendLine(f._2 + " " + f._1)
    })
  }
}
