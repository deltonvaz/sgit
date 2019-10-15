import better.files.File
import better.files.File.currentWorkingDirectory
import org.scalatest.FunSuite

import scala.collection.immutable.SortedSet


/*

  A tree file must contains
  type sha fileName
  blob 9b3a97dafadb12faf10cf1a1f3a32f63eaa7220a	foo.txt
 */
class TreeTest extends FunSuite {
  test("Sgit should verify if the file which is being added exists") {
    val addfile = (currentWorkingDirectory/"example.txt")

    val sha_1 = addfile.sha1

    val content = addfile.contentAsString

    val length = addfile.size

    println("Content length " + length)

    val headerLine = Object.HeaderLine(ObjectType.Tree, length)
    println(headerLine)

    val blob = new Blob(sha_1, content, "example.txt")

    //var obj = List(blob)

    val tree = new Tree("", Map("example.txt" -> "aaa"))
    //val tree = new Tree("", List("AAAAA"))

    val afl = File.newTemporaryFile().appendLine(tree.treeHeader)

    println(afl.contentAsString)

    println("arquivo temp " + afl)
    //val blob = new Blob(addfile.sha1, "example.txt")

    //println(currentWorkingDirectory/".sgit")


//    println("DIRECTORY!: "+ (currentWorkingDirectory/".sgit"/"objects"/sha_1).toString())
//
//    val filePath = currentWorkingDirectory.toString() + "/.sgit/objects/" + sha_1
//
//    println(filePath)
//
//    //val f = File(filePath)
//    (currentWorkingDirectory/".sgit"/"objects"/sha_1)
//      .createIfNotExists()
//      .appendLine(headerLine)
//      .appendLine(content)
//      .changeExtensionTo(".blob")


  }
//
//  test("Sgit should have permissions write/read .sgit dir") {
//    assert(FileHandler.hasPermission)
//  }
}