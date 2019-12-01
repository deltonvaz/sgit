package objects

import better.files.File
import functions.Constants

final case class Tree(override val id : File, override val workingDir: String, var entries : Map[String, String]) extends Object
{
  override def objectType: ObjectType.Tree.type = ObjectType.Tree

  def treeHeader:String = {
    var retVal = ""
    entries.foreach(obj => {
      retVal = retVal + s"${obj._2} ${obj._1}\n" //id -> fileName
    })
    retVal
  }

  def save() : File = {
    var retVal : File = File(workingDir)
    File.usingTemporaryFile() { tempFile =>
      tempFile.append(treeHeader)

      retVal = (File(workingDir)/Constants.OBJECTS_FOLDER/tempFile.sha1)
        .createIfNotExists()
        .clear()
        .append(treeHeader)
    }
    retVal
  }
}
