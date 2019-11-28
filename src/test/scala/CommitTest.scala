import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import misc.{CommitHandler, Constants, FileHandler}
import org.scalatest.{BeforeAndAfter, FlatSpec, Outcome}

class CommitTest extends FlatSpec with BeforeAndAfter {
  val fileName1 = "file1.txt"
  val fileName2 = "file2.txt"
  val workingPath : File = mkdirs(cwd/"testFolder")
  val workingDirectory : String = workingPath.path.toString
  val sgit = Sgit(workingPath.path.toString)
  val file1 : File  = (workingPath/fileName1).createIfNotExists()
  val file2 : File = (workingPath/fileName2).createIfNotExists()
  val index : File = workingPath/".sgit"/"INDEX"
  val fileHandler : FileHandler = FileHandler(workingPath)
  val testCommit = CommitHandler(workingDirectory)


  override def withFixture(test: NoArgTest): Outcome = {
    try super.withFixture(test)
    finally {
      workingPath.deleteOnExit()
      workingPath.delete()
    }
  }

  before {
    sgit.init()
  }

  "A commit" should "verify if is the first commit" in {
    (File(workingDirectory)/Constants.SGIT_ROOT/"HEAD").clear()
    assert(testCommit.isFirstCommit, true)
  }

  "As a first commit the head file" should "be empty" in {
    (File(workingDirectory)/Constants.DEFAULT_HEAD_PATH).clear()
    assert((File(workingDirectory)/Constants.DEFAULT_HEAD_PATH).isEmpty, true)
  }

  it should "insert into head ref the last commit reference" in {
    sgit.commit("first commit")
  }

  "As a second commit without any new file" should "points to new commit file" in {
    sgit.commit("first commit")
    sgit.commit("second commit")
  }


}