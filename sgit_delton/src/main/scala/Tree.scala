import better.files.File

//final case class Tree(override val id : String, var entries : Map[String, Object]) extends Object
final case class Tree(override val id : String, var entries : Map[String, String]) extends Object
{

  final override def objectType = ObjectType.Tree


  def treeHeader:String = {
    var retVal = ""
    entries.foreach(obj => {
      //(sha filename)
      //retVal += obj
      retVal = retVal + s"${obj._2} ${obj._1}\n" //id -> fileName
      //retVal += s"${obj.id} \t${obj.objectType}"
    })
    retVal
  }

//  def find(sha1 : String) : Option[Object] = {
//    if (entries.contains(sha1)) {
//      Some(entries(sha1))
//    }
//    None
//  }

  //TODO verify
  def add(entry : Object) : Unit = {
    if(!entries.contains(entry.id)){
      //entries = entry.id :: entries
      entries = entries + (entry.id -> entry.id)
    }
  }

//  def getListing: List[Object] = {
//    var ans:List[Object] = List[Object]()
//
//    entries.values.foreach(objName =>
//      ans = objName :: ans
//    )
//    ans
//  }

  /*
    When a tree is saved it should return the SHA of the created tree
   */
  def save(objs : List[Object]): String = {
    val afl = File.newTemporaryFile()
    objs.foreach(obj => {
      obj.objectType match {
        case ObjectType.Blob => {
          val b = obj.asInstanceOf[Blob]
          afl.appendLine(obj.objectType+"\t"+obj.id+"\t"+b.getTitle)
        }
          /*
          Check this*/

//        case ObjectType.Tree => {
//          val b = obj.asInstanceOf[Tree]
//          afl.appendLine(obj.objectType+"\t"+obj.id+"\t"+b.getTitle)
//        }

      }
    })
    afl.sha1
  }
}