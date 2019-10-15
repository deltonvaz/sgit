import java.io.FileNotFoundException

import org.scalatest.FunSuite

class AddTest extends FunSuite {

  test("Sgit should verify if the file which is being added exists") {
    assert(!FileHandler.fileExists("alface"))
  }

  test("Sgit should throw an exception when file does not exists") {
    assertThrows[FileNotFoundException] {
      Sgit.add("arquivo.txt")
    }

  }

  test("Sgit should add") {
    Sgit.add("example - cópia.txt")
    //Sgit.add("teste/alface.txt")
    //assert(!FileHandler.fileExists("alface"))
  }

}