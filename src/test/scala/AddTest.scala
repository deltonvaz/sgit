import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import misc.FileHandler
import org.scalatest.{BeforeAndAfter, FlatSpec}

class AddTest extends FlatSpec with BeforeAndAfter {

  val fileName1 = "nome com espaco.txt"
  val fileName2 = "file2.txt"
  val workingPath : File = mkdirs(cwd/"testFolder")
  val sgit = Sgit(workingPath.path.toString)
  val file1 : File  = (workingPath/fileName1).createIfNotExists()
  val file2 : File = (workingPath/fileName2).createIfNotExists()
  val index : File = workingPath/".sgit"/"INDEX"
  val fileHandler : FileHandler = FileHandler(workingPath)

  behavior of "add sgit method"

  it should "return error when the file does not exists" in {
    Sgit(workingPath.path.toString).init()

    Sgit(workingPath.path.toString).add("dontExists")
  }

  ignore
  it should "create a single blob when single document is added" in {
    Sgit(workingPath.path.toString).add("alface.txt")
  }

  ignore
  it should "create a single blob when single document inside a folder is added" in {
    Sgit(workingPath.path.toString).add("example.txt")
  }

  it should "create multiple blobs when . is used as parameter to add" in {

    file1.appendLine("tst1")
    file2.appendLine("tst2")

    Sgit(workingPath.path.toString).add(".")

    assert((workingPath/".sgit"/"objects"/file1.sha1).exists)
    assert((workingPath/".sgit"/"objects"/file2.sha1).exists)

  }

  "IN THEEE EEEEEEND" should "delete test folders" in {
    workingPath.deleteOnExit()
    workingPath.delete()
    assert(!workingPath.isDirectory)
  }

}