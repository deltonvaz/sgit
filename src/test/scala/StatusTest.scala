import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import misc.{FileHandler, Functions}
import org.scalatest.{BeforeAndAfter, FlatSpec, Outcome}

class StatusTest extends FlatSpec with BeforeAndAfter {

  val fileName1 = "fileName1.txt"
  var workingPath : File = File.home
  var file1 : File = workingPath

  var uniqueFileName : Seq[String] = Seq()

  override def withFixture(test: NoArgTest): Outcome = {
    try super.withFixture(test)
    finally {
      workingPath.deleteOnExit()
      workingPath.delete()
    }
  }

  before {
    workingPath = mkdirs(cwd/"testFolder")
    uniqueFileName = Seq(fileName1)
    assertResult(FileHandler(workingPath).createGitBaseFiles()) (true)
  }

  "when it is the first commit" should "show that there is no commits yet" in {
    assertResult(
      Functions.status(workingPath)
        ._2) (
      Map(
        "firstCommit" -> true,
        "changes" -> false,
        "modified" -> false,
        "untracked" -> false)
    )
  }

  "when there is untracked files" should "should show name of untracked files" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    assertResult(
      Functions.status(workingPath)._2
    ) (
      Map(
        "firstCommit" -> true,
        "changes" -> false,
        "modified" -> false,
        "untracked" -> true)
    )
  }

  "when there is added files" should "should show name of added files" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    Functions.add(workingPath, uniqueFileName)
    assertResult(
      Functions.status(workingPath)._2
    ) (
      Map(
        "firstCommit" -> true,
        "changes" -> true,
        "modified" -> false,
        "untracked" -> false)
    )
  }

  "when there is modified files" should "should show name of modified files" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    Functions.add(workingPath, uniqueFileName)
    file1.appendLine("a new line to test")
    assertResult(
      Functions.status(workingPath)._2
    ) (
      Map(
        "firstCommit" -> true,
        "changes" -> true,
        "modified" -> true,
        "untracked" -> false)
    )
  }

  "when there is deleted files from stage area" should "should show name of deleted files" in {
    file1 = (workingPath/fileName1).createIfNotExists()
    Functions.add(workingPath, uniqueFileName)
    file1.delete()
    assertResult(
      Functions.status(workingPath)._2
    ) (
      Map(
        "firstCommit" -> true,
        "changes" -> true,
        "modified" -> true,
        "untracked" -> false)
    )
  }

}