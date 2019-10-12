final case class Blob(override val id : String, content : String, objectTitle : String) extends Object {

  final override def objectType = ObjectType.Blob

  def getTitle : String = objectTitle
}
