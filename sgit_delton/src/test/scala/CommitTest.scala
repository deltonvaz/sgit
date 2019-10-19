import better.files.File
import misc.{CommitHandler, Constants}
import org.scalatest.{BeforeAndAfter, FlatSpec}

class CommitTest extends FlatSpec with BeforeAndAfter {
  val workingDirectory = "/Users/delton/sgit_tests"
  var testCommit = CommitHandler(workingDirectory)


  before {
    //Generate index file
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
    val sgit = Sgit(workingDirectory)
    sgit.commit("first commit")
  }

  "As a second commit without any new file" should "points to new commit file" in {
    val sgit = Sgit(workingDirectory)
    sgit.commit("second commit")
  }

}