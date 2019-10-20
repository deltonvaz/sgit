import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import org.scalatest.{BeforeAndAfter, FlatSpec}

class StatusTest extends FlatSpec with BeforeAndAfter {

  val workingPath : File = mkdirs(cwd/"testFolder")
  val sgit = Sgit(workingPath.path.toString)
  val fileName1 = "nome com espaco.txt"
  val fileName2 = "file2.txt"
  val folderName = "folder"
  val folder : File  = (workingPath/folderName).createDirectoryIfNotExists()
  val file1 : File  = (workingPath/folderName/fileName1).createFileIfNotExists()
  val file2 : File = (workingPath/fileName2).createIfNotExists()

  before {
  }

  behavior of "sgit status"
  it should "show status of the system" in {
    sgit.init()
    sgit.add(file1.path.toString)
    sgit.add(fileName2)
    sgit.status()
  }

  "IN THEEE EEEEEEND" should "delete test folders" in {
    workingPath.deleteOnExit()
    workingPath.delete()
    assert(!workingPath.isDirectory)
  }

}