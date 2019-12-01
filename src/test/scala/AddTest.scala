import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import misc.{Constants, FileHandler, Functions, StageHandler}
import org.scalatest.{BeforeAndAfter, FlatSpec, Outcome}

class AddTest extends FlatSpec with BeforeAndAfter {

  val fileName1 = "fileName1.txt"
  val fileName2 = "file2.txt"
  val fileName3 = ".wierdfileNˆa*me.txt"
  var workingPath : File = File.home
  var file1 : File = workingPath
  var file2 : File = workingPath
  var file3 : File = workingPath

  var emptyFileName: Seq[String] = Seq()
  var nonExistendFileName: Seq[String] = Seq()
  var uniqueFileName : Seq[String] = Seq()
  var allFiles : Seq[String] = Seq()

  override def withFixture(test: NoArgTest): Outcome = {
    try super.withFixture(test)
    finally {
      workingPath.deleteOnExit()
      workingPath.delete()
    }
  }

  before {
    workingPath = mkdirs(cwd/"testFolder")
    emptyFileName = Seq("")
    nonExistendFileName = Seq("dontExists")
    uniqueFileName = Seq(fileName1)
    allFiles = Seq(".")
    assertResult(FileHandler(workingPath).createGitBaseFiles()) (true)
  }

  it should "return error when the file does not exists" in {
    assertResult(Functions.add(workingPath, nonExistendFileName)) (nonExistendFileName.mkString("")+" "+Constants.MSG_NOT_FOUND, false)
  }

  it should "create a single blob when single document is added" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    assertResult(Functions.add(workingPath, uniqueFileName)) ("", true)
    assert((workingPath/Constants.OBJECTS_FOLDER/file1.sha1).exists)
  }

  it should "create multiple blobs when . is used as parameter to add" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    file2 = (workingPath/fileName2).createIfNotExists()
    file3 = (workingPath/fileName3).createIfNotExists()
    file1.appendLine("tst1")
    file2.appendLine("tst2")
    file3.appendLine("fileNumber3")
    assertResult(Functions.add(workingPath, allFiles)) ("", true)
    assert((workingPath/Constants.OBJECTS_FOLDER/file1.sha1).exists)
    assert((workingPath/Constants.OBJECTS_FOLDER/file2.sha1).exists)
    assert((workingPath/Constants.OBJECTS_FOLDER/file3.sha1).exists)
  }

  it should "should add pre-removed files from stage area" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    file1.appendLine("tst1")
    assertResult(Functions.add(workingPath, allFiles)) ("", true)
    file1.delete()
    assertResult(StageHandler(workingPath.pathAsString).removeStagged(fileName1)) (true)
  }

}