import java.text.SimpleDateFormat
import java.util.Calendar

final case class Commit(override val id : String, pointToCommit : String, message : String) extends Object {
  override def objectType: ObjectType.Commit.type = ObjectType.Commit
  var dateTime : String = ""

  def commitHeader:String = {
    val formatDate = new SimpleDateFormat("yyyy-MM-dd:hhmmss")
    val now  = Calendar.getInstance().getTime

    s"$id $pointToCommit ${formatDate.format(now)} $message"
  }

  def setDateTime(dateTime : String) : Unit = {
    this.dateTime = dateTime
  }

}
