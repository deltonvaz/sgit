import org.scalatest.FunSuite

class FileHandlerTest extends FunSuite {

  test("Sgit should verify if the file which is being added exists") {
    assert(!FileHandler.fileExists("alface"))
    FileHandler.createGitBaseFiles()
  }

    test("Sgit should have permissions write/read .sgit dir") {
    assert(FileHandler.hasPermission)
  }
  //
//  test("Sgit should have permissions write/read .sgit dir") {
//    assert(FileHandler.hasPermission() == "rwxr-xr-x")
//  }
//
//  test("Invoking head on an empty Set should produce NoSuchElementException") {
//    assertThrows[NoSuchElementException] {
//      Set.empty.head
//    }
//  }
}