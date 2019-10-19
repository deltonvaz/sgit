import org.scalatest.{FlatSpec, FunSuite}
import better.files._
import better.files.Dsl._
import misc.FileHandler


class DiffTest extends FlatSpec {

  val fileName1 = "file1.txt"
  val fileName2 = "file2.txt"
  val sgitDir : File = mkdirs(cwd/"testFolder")
  val sgit = Sgit(sgitDir.path.toString)
  val file1 : File  = (sgitDir/fileName1).createIfNotExists()
  val file2 : File = (sgitDir/fileName2).createIfNotExists()
  val index : File = sgitDir/".sgit"/"INDEX"
  val fileHandler : FileHandler = FileHandler(sgitDir)

  "at the beginning the system" should "create sgit base folders" in {
    assert(sgitDir.isDirectory)
    sgit.init()

  }

  "when a diff is wanted" should "files must be created" in {
    assert(file1.isRegularFile)
    assert(file2.isRegularFile)
  }

  "when add file to stage" should "create index file with 2 files" in {
    file1.appendLine("a new line").appendLine("uma nova linha com muitas coisas")
    file2.appendLine("alface").appendLine("com sorvete")
    sgit.add(file1.path.toString)
    sgit.add(file2.path.toString)
    assert(index.lines.size == 2)
  }

  //"when a file is not modified the diff"
  ignore should "return an empty string" in {
    fileHandler.getDiffLinesWithStaged
  }

  "when a file is modified the diff" should "show lines that have been modified" in {
    file1.appendLine("a new line").appendLine("uma nova linha")
    file2.appendLine("alface").appendLine("com sorvete")
    sgit.diff()
  }

//  "when the diff command is called" should "print the old and new line of the file" in {
//    //sgit.diff()
//  }

  "when the diff command is called" should "should show deleted lines" in {
    file1.clear()
    file1.appendLine("a new line")
    sgit.diff()
  }

  "IN THEEE EEEEEEND" should "delete test folders" in {
    sgitDir.deleteOnExit()
    sgitDir.delete()
    assert(!sgitDir.isDirectory)
  }

}
