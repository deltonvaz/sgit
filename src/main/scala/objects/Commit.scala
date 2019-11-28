package objects

import java.text.SimpleDateFormat
import java.util.Calendar

import better.files.File
import misc.Constants

final case class Commit(override val id : File, override val workingDir: String, pointToCommit : String, message : String) extends Object {
  override def objectType: ObjectType.Commit.type = ObjectType.Commit
  var dateTime : String = ""

  def commitHeader:String = {
    val formatDate = new SimpleDateFormat("yyyy-MM-dd:hhmmss")
    val now  = Calendar.getInstance().getTime
    val user = System.getProperty("user.name")

    s"${id.sha1} $pointToCommit ${formatDate.format(now)} $message $user" //tree parentCommit
  }

  def setDateTime(dateTime : String) : Unit = {
    this.dateTime = dateTime
  }

  def save : String = {
    //Generate SHA for the new commit
    var commitSHA : String = ""
    File.usingTemporaryFile() {tempFile =>
      tempFile.appendLine(commitHeader)
      commitSHA = tempFile.sha1
      val tree = (File(workingDir)/Constants.OBJECTS_FOLDER/commitSHA)
        .createIfNotExists()
        .appendLine(commitHeader)

      commitSHA = tree.sha1
    }
    commitSHA
  }

}
