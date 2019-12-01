import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import functions.{CommitHandler, Constants, FileHandler, Functions}
import org.scalatest.{BeforeAndAfter, FlatSpec, Outcome}

class CommitTest extends FlatSpec with BeforeAndAfter {
  val fileName1 = "file1.txt"
  val fileName2 = "file2.txt"
  var workingPath : File = File.home
  var workingDirectory : String = ""

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
    workingDirectory = workingPath.path.toString
    assertResult(FileHandler(workingPath).createGitBaseFiles()) (true)
  }

  "A commit" should "verify if is the first commit" in {
    assert(CommitHandler(workingDirectory).isFirstCommit, true)
  }

  "As a first commit the head file" should "be empty" in {
    (File(workingDirectory)/Constants.DEFAULT_HEAD_PATH).clear()
    assert((File(workingDirectory)/Constants.DEFAULT_HEAD_PATH).isEmpty, true)
  }

  "when it is first commit and nothing have been added" should "return that there is nothing to commit" in {
    assertResult(
      Functions.commit(workingPath, "first commit")._2
    ) (Map(
      "firstCommit" -> true,
      "sync" -> true))
  }

  "when NOT first commit but stage area _IS_ sync" should "return that there are nothing to commit" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    Functions.add(workingPath, Seq(fileName1))
    Functions.commit(workingPath, "first commit")

    val testResult = Functions.commit(workingPath, "2nd commit")
    assertResult(
      testResult._2
    ) (Map(
      "firstCommit" -> false,
      "sync" -> true))
  }

  "when NOT first commit and stage area is _NOT_ sync" should "return that there are files to commit" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    Functions.add(workingPath, Seq(fileName1))
    Functions.commit(workingPath, "first commit")

    file2 = (workingPath/fileName2).createIfNotExists()
    Functions.add(workingPath, Seq(fileName2))
    val testResult = Functions.commit(workingPath, "2nd commit")
    assertResult(
      testResult._2
    ) (Map(
      "firstCommit" -> false,
      "sync" -> false))
  }

}