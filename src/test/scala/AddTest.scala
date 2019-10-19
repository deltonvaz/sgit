import better.files.File
import org.scalatest.{BeforeAndAfter, FlatSpec}

class AddTest extends FlatSpec with BeforeAndAfter {

  var workingPath : File = File("/Users/delton/sgit_tests")
  var file1 : File = _
  var file2 : File = _

  behavior of "add sgit method"

  it should "return error when the file does not exists" in {
    Sgit(workingPath.path.toString).init()

    Sgit(workingPath.path.toString).add("dontExists")
  }

  it should "create a single blob when single document is added" in {
    Sgit(workingPath.path.toString).add("alface.txt")
  }

  it should "create a single blob when single document inside a folder is added" in {
    Sgit(workingPath.path.toString).add("example.txt")
  }

  it should "create multiple blobs when . is used as parameter to add" in {

    file1 = (workingPath/"file1.txt").appendLine("tst1")
    file2 = (workingPath/"file2.txt").appendLine("tst2")

    Sgit(workingPath.path.toString).add(".")

    assert((workingPath/".sgit"/"objects"/file1.sha1).exists)
    assert((workingPath/".sgit"/"objects"/file2.sha1).exists)

    file1.deleteOnExit()
    file2.deleteOnExit()

  }

}