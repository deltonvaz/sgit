import better.files.File
import org.scalatest.FunSuite

class StatusTest extends FunSuite {

  var workingPath : String = File("/Users/delton/sgit_tests").path.toString

  test("Sgit should status") {
    Sgit(workingPath).status()
  }

}