package objects

import better.files.File
import misc.Constants

final case class Blob(override val id : File, override val workingDir: String) extends Object {

  final override def objectType = ObjectType.Blob

  def getTitle : String = id.name

  val workDir = File(workingDir)

  val stageArea: File = workDir/Constants.SGIT_ROOT/"INDEX"


  def save() : Unit = {
    (workDir/Constants.SGIT_ROOT/"objects"/id.sha1)
      .createIfNotExists()
      .clear()
      .appendLine(id.contentAsString)
    addToStageArea()
  }

  def addToStageArea() : Unit = {
    var stagedLines : Map[String, String] = Map()
    val pathFile : String = id.path.toString diff (workingDir+"/")

    stagedLines = stageArea.lines
      .map(_.split(" "))
      .map {
        case Array(k, v) => (v, k)//FilePath -> SHA
      }.toMap

    //Check if SHA is already registred into staged area
    stagedLines = stagedLines.+(pathFile -> id.sha1)

    stageArea.clear()
    stagedLines.foreach(f => {
      stageArea.appendLine(f._2 + " " + f._1)
    })
  }
}
