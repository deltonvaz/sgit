package misc

import better.files.File
import org.scalatest.FunSuite

class FileHandlerTest extends FunSuite {

  val path = "/Users/delton/sgit_tests"

  test("Sgit should verify if the file which is being added exists") {
    assert(!FileHandler(File(path)).fileExists("alface"))
    FileHandler(File(path)).createGitBaseFiles()
  }

  test("testGetModifiedFilesFromWorkingDirectory") {
    FileHandler(File(path)).getModifiedFilesFromWorkingDirectory
  }

}
