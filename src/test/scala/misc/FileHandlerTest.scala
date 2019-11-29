package misc

import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import org.scalatest.FunSuite

class FileHandlerTest extends FunSuite {

  val workingPath : File = mkdirs(cwd/"testFolder")

  val path : String = workingPath.path.toString

  test("Sgit should verify if the file which is being added exists") {
    assert(!FileHandler(File(path)).fileExists("alface"))
    FileHandler(File(path)).createGitBaseFiles()
  }

  test("testGetModifiedFilesFromWorkingDirectory") {
    FileHandler(File(path)).getModifiedFilesFromWorkingDirectory(false)
  }

  test("in the end delete all files") {
    workingPath.deleteOnExit()
    workingPath.delete()
    assert(!workingPath.isDirectory)
  }

}
