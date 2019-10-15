import better.files.File
import better.files.File.currentWorkingDirectory

import scala.util.matching.Regex

object Constants {
  val IGNORE_ROOT_FILES: Set[String] = Set(".DS_Store", "target", "project", "build.sbt", ".idea", "src", ".sgit")
  val STAGE_PATTERN : Regex = """(\w+) +(\w+.+)""".r
  val COMMIT_PATTERN : Regex = """(\w+)\s(\w+)\s([^\s]+)\s(.+)""".r
  val BRANCH_PATTERN : Regex = """(.+\/)(.+)""".r
  val OBJECTS_FOLDER : File = currentWorkingDirectory/".sgit"/"objects"
  val DEFAULT_HEAD_PATH : String = "refs/heads/master"
}
