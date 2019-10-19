import org.scalatest._
import better.files._

import objects.Blob

class BlobTest extends FlatSpec {

  "When create a blob" should "insert a file in objects folder" in {
    //val b = objects.Blob("SHA-1Content", "titulo")

    val addfile = File("/Users/delton/sgit_tests/example.txt")

    val sha_1 = addfile.sha1


    var bolb = Blob(addfile, "/Users/delton/sgit_tests")//"/Users/delton/sgit_tests")

    bolb.addToStageArea

//    val content = addfile.contentAsString

//    val length = addfile.size
//
//    println("Content length " + length)
//
//    val headerLine = objects.Object.HeaderLine(objects.ObjectType.objects.Blob, length)
//    println(headerLine)
//
//
//    //val blob = new objects.Blob(addfile.sha1, "example.txt")
//
//    //println(currentWorkingDirectory/".sgit")
//
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


    //headerLine should equal ("blob 1234")
//    assert(stack.pop() === 2)
//    assert(stack.pop() === 1)
  }



}