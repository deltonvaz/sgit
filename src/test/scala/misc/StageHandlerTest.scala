package misc

import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class StageHandlerTest extends FunSuite with BeforeAndAfterEach {
  val workingDir : File = mkdirs(cwd/"testFolder")
  val workingPath : String = workingDir.path.toString
  val stage : StageHandler = StageHandler(workingPath)
  override def beforeEach() {

    //val stage : StageHandler = StageHandler(workingPath)
  }


  test("testGetStagedFileLines") {

  }

  test("testIsStageSync") {

  }

  test("testWorkDir") {

  }

  test("testGetStagedFiles") {

  }

  test("testGetStageBLOBS") {

  }

  test("testStageArea") {

  }

  test("testWorkingDir") {

  }

  test("testGetStagedFilesName") {

  }

  test("testGetModifiedFilesInStage") {
    //stage.getModifiedFilesInStage
  }

  test("in the end delete all files") {
    workingDir.deleteOnExit()
    workingDir.delete()
    assert(!workingDir.isDirectory)
  }


}
