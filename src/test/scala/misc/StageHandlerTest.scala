package misc

import better.files.File
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class StageHandlerTest extends FunSuite with BeforeAndAfterEach {

  var workingPath : String = File("/Users/delton/sgit_tests").path.toString
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
    stage.getModifiedFilesInStage
  }


}
