package misc
import scala.util.matching.Regex

object Constants {
  val SGIT_ROOT: String = ".sgit"
  val STAGE_PATTERN : Regex = """(\w+) +(\w+.+)""".r
  val COMMIT_PATTERN : Regex = """(\w+)\s(\w+)\s([^\s]+)\s(.+)""".r
  val BRANCH_PATTERN : Regex = """(.+\/)(.+)""".r
  val OBJECTS_FOLDER : String = ".sgit/objects"
  val DEFAULT_HEAD_PATH : String = ".sgit/refs/heads/master"
  val SGIT_HEADS : String = ".sgit/refs/heads"
  val SGIT_TAGS : String = ".sgit/refs/tags"
  val NEW : Int = 0
  val MODIFIED : Int = 1
  val DELETED : Int = 2
}
