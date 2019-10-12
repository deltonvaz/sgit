final case class Commit(override val id : String, content : String, objectTitle : String) extends Object {
  final override def objectType = ObjectType.Blob

}
