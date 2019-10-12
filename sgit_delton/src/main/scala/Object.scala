import scala.util.matching.Regex

trait Object
{
  def objectType : ObjectType
  def id : String //SHA-1 ID
}

object Object
{
  val HeaderPattern: Regex = """(\w+) +(\d+)""".r

  object HeaderLine
  {
    import java.lang.Long.parseLong

    def apply(objectType : ObjectType, contentLength : Long) : String =
    {
      require (contentLength >= 0L)
      s"$objectType $contentLength"
    }

    def unapply(s : String) : Option[(ObjectType, Long)] = s match
    {
      case HeaderPattern(typeId, contentLengthString) => typeId match
      {
        case ObjectType(objectType) =>
          try {Some(objectType, parseLong(contentLengthString))}
          catch {case _ : NumberFormatException => None}
        case _ => None
      }
      case _ => None
    }
  }

}