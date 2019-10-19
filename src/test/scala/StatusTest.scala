import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import org.scalatest.{BeforeAndAfter, FlatSpec}

class StatusTest extends FlatSpec with BeforeAndAfter {

  val workingPath : File = mkdirs(cwd/"testFolder")
  val sgit = Sgit(workingPath.path.toString)

  before {
    sgit.init()
  }

  behavior of "sgit status"

  it should "show status of the system" in {
    sgit.status()
  }

  "IN THEEE EEEEEEND" should "delete test folders" in {
    workingPath.deleteOnExit()
    workingPath.delete()
    assert(!workingPath.isDirectory)
  }

}