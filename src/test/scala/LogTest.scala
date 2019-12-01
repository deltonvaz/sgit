import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import functions.{CommitHandler, Constants, FileHandler, Functions}
import org.scalatest.{BeforeAndAfter, FlatSpec, Outcome}

class LogTest extends FlatSpec with BeforeAndAfter {
  val fileName1 = "file1.txt"
  val fileName2 = "file2.txt"
  var workingPath : File = File.home
  var commitHandler : CommitHandler = _

  var file1 : File = File.home
  var file2 : File = File.home

  override def withFixture(test: NoArgTest): Outcome = {
    try super.withFixture(test)
    finally {
      workingPath.deleteOnExit()
      workingPath.delete()
    }
  }

  before {
    workingPath = mkdirs(cwd/"testFolder")
    commitHandler = CommitHandler(workingPath.pathAsString)
    assertResult(FileHandler(workingPath).createGitBaseFiles()) (true)
  }

  "When it is first commit" should "return an empty string" in {
    assertResult(commitHandler.getCommitsHistoric(true, true)) (false)
  }

  "when there are two commits" should "show return its details" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    file2 = (workingPath/fileName2).createIfNotExists()
    Functions.add(workingPath, Seq(fileName1))
    Functions.commit(workingPath, "firstCommit")
    assert(commitHandler.getCommitsHistoric(true, true), true)
    file1.appendLine("newLineString")
    Functions.add(workingPath, Seq(fileName2))
    Functions.add(workingPath, Seq(fileName1))
    Functions.commit(workingPath, "secondCommit")
    assert(commitHandler.getCommitsHistoric(true, true), true)
  }

}