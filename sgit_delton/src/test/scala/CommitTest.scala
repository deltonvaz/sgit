import org.scalatest.{BeforeAndAfter, FunSuite}

class CommitTest extends FunSuite with BeforeAndAfter {
  var testSgit = Sgit


  before {
    testSgit.initialize()
  }

  test("Sgit should commit") {
    testSgit.commit("first commit")
  }

}