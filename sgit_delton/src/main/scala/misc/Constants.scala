package misc

import better.files.File

import scala.util.matching.Regex

object Constants {
  val IGNORE_ROOT_FILES: Set[String] = Set(".DS_Store", "target", "project", "build.sbt", ".idea", "src", ".sgit")
  val SGIT_ROOT: String = ".sgit"
  val STAGE_PATTERN : Regex = """(\w+) +(\w+.+)""".r
  val COMMIT_PATTERN : Regex = """(\w+)\s(\w+)\s([^\s]+)\s(.+)""".r
  val BRANCH_PATTERN : Regex = """(.+\/)(.+)""".r
  val OBJECTS_FOLDER : String = ".sgit/objects"
  val DEFAULT_HEAD_PATH : String = ".sgit/refs/heads/master"
  val SGIT_HEADS : String = ".sgit/refs/heads"
  val WORKING_DIRECTORY : File = File("/Users/delton/sgit_tests")
  val NEW : Int = 0
  val MODIFIED : Int = 1
  val DELETED : Int = 2
}
