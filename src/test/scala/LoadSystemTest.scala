import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import org.scalatest.{BeforeAndAfter, FlatSpec}

class LoadSystemTest extends FlatSpec with BeforeAndAfter {

  val workingPath : File = mkdirs(cwd/"testFolder")

  behavior of "load sgit system"

  it should "return false if there is not a sgit repository" in {
    assert(Sgit(workingPath.path.toString).loadSystem === false)
  }

  it should "return true if sgit has permission to create new files/folders" in {
    (workingPath/".sgit").createDirectoryIfNotExists()
    assert(Sgit(workingPath.path.toString).loadSystem === true)
  }

}