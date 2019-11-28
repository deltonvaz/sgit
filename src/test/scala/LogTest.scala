import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import misc.{CommitHandler, Constants, FileHandler}
import org.scalatest.{BeforeAndAfter, FlatSpec, Outcome}

class LogTest extends FlatSpec with BeforeAndAfter {
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
  var commitHandler : CommitHandler = _


  override def withFixture(test: NoArgTest): Outcome = {
    try super.withFixture(test)
    finally {
      workingPath.deleteOnExit()
      workingPath.delete()
    }
  }

  before {
    sgit.init()
    commitHandler = CommitHandler(workingDirectory)
  }

  "When it is first commit" should "return an empty string" in {
    assertResult(commitHandler.getCommitsHistoric(true)) (false)
  }

  "when there is two commits" should "show it's details" in {
    sgit.add(Seq(fileName1))
    sgit.commit("first commit")
    sgit.add(Seq(fileName2))
    sgit.commit("second commit")
    assert(commitHandler.getCommitsHistoric(true), true)
  }

  it should "insert into head ref the last commit reference" in {
    sgit.commit("first commit")
  }

  "As a second commit without any new file" should "points to new commit file" in {
    sgit.commit("first commit")
    sgit.commit("second commit")
  }


}