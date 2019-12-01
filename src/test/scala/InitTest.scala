import java.nio.file.attribute.PosixFilePermission

import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import functions.FileHandler
import org.scalatest.{BeforeAndAfter, FlatSpec, Outcome}

class InitTest extends FlatSpec with BeforeAndAfter {

  var workingPath : File = File.home

  /**
    * withFixture composed of the objects and other artifacts
    * (files, sockets, database connections, etc.)
    * tests use to do their work
    * @param test
    * @return
    */
  override def withFixture(test: NoArgTest): Outcome = {
    try super.withFixture(test)
    finally {
      workingPath.deleteOnExit()
      workingPath.delete()
    }
  }

  /**
    * Lets assume that all tests are being made in a testFolder
    */
  before {
    workingPath = mkdirs(cwd/"testFolder")
  }


  "when init starts correctly" should "create all base files correctly" in {
    assert(FileHandler(workingPath).createGitBaseFiles(), true)
  }

//  "when init dont have right to create folders" should "return an error message" in {
//    workingPath.removePermission(PosixFilePermission.OWNER_WRITE)
//    assertResult(FileHandler(workingPath).createGitBaseFiles()) (false)
//  }

  "when the sgit base folder is already created" should "return true" in {
    FileHandler(workingPath).createGitBaseFiles()
    assertResult(FileHandler(workingPath).isGitBaseCreated) (true)
  }

}