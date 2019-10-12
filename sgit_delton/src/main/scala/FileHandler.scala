import better.files.File
import better.files.File._
import better.files._
import better.files.File.currentWorkingDirectory

object FileHandler {
  def isGitBaseCreated: Boolean = {
    (currentWorkingDirectory/".sgit/").exists
  }

  def createGitBaseFiles(): Unit = {
    (currentWorkingDirectory/".sgit").createIfNotExists(true)
    (currentWorkingDirectory/".sgit"/"HEAD").createFileIfNotExists(true)
    (currentWorkingDirectory/".sgit"/"INDEX").createFileIfNotExists(true)
    (currentWorkingDirectory/".sgit"/"objects").createDirectoryIfNotExists(true)
    (currentWorkingDirectory/".sgit"/"refs").createDirectoryIfNotExists(true)
    (currentWorkingDirectory/".sgit"/"refs"/"heads").createDirectoryIfNotExists(true)
    (currentWorkingDirectory/".sgit"/"refs"/"tags").createDirectoryIfNotExists(true)
    //true
  }

  def hasPermission: Boolean = {
    (currentWorkingDirectory/".sgit/").permissionsAsString == "rwxr-xr-x"
  }

  def systemVerify(): Unit = {
    if(!FileHandler.isGitBaseCreated) println(s"Reinitialized existing Sgit repository in $currentWorkingDirectory.sgit")
  }

  def fileExists(fileName : String): Boolean = {
    (currentWorkingDirectory/fileName).isRegularFile
  }

}
