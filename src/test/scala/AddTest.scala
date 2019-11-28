import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import misc.{Constants, FileHandler, Functions}
import org.scalatest.{BeforeAndAfter, FlatSpec, Outcome}

class AddTest extends FlatSpec with BeforeAndAfter {

  val fileName1 = "nome com espaco.txt"
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

  behavior of "add sgit method"

  override def withFixture(test: NoArgTest): Outcome = {
    try super.withFixture(test)
    finally {
      workingPath.deleteOnExit()
      workingPath.delete()
    }
  }

  before {
    workingPath = mkdirs(cwd/"testFolder")
    file1 = (workingPath/fileName1).createIfNotExists()
    file2 = (workingPath/fileName2).createIfNotExists()
    file3 = (workingPath/fileName3).createIfNotExists()

    emptyFileName = Seq("")
    nonExistendFileName = Seq("dontExists")
    uniqueFileName = Seq(fileName1)
    allFiles = Seq(".")

    Sgit(workingPath.path.toString).init()
  }

  it should "return error when the file does not exists" in {
    assertResult(Functions.add(workingPath, nonExistendFileName)) (nonExistendFileName.mkString("")+" "+Constants.MSG_NOT_FOUND, false)
  }

  it should "create a single blob when single document is added" in {
    assertResult(Functions.add(workingPath, uniqueFileName)) ("", true)
    assert((workingPath/Constants.OBJECTS_FOLDER/file1.sha1).exists)
  }

  it should "create multiple blobs when . is used as parameter to add" in {
    file1.appendLine("tst1")
    file2.appendLine("tst2")
    assertResult(Functions.add(workingPath, allFiles)) ("", true)
    assert((workingPath/Constants.OBJECTS_FOLDER/file1.sha1).exists)
    assert((workingPath/Constants.OBJECTS_FOLDER/file2.sha1).exists)
    assert((workingPath/Constants.OBJECTS_FOLDER/file3.sha1).exists)
  }


}