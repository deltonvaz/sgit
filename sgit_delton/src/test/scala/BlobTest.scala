import org.scalatest._
import better.files._
import File._
import java.security.MessageDigest

class BlobTest extends FlatSpec {

  "When create a blob" should "insert a file in objects folder" in {
    //val b = Blob("SHA-1Content", "titulo")

    val addfile = (currentWorkingDirectory/"example.txt")

    val sha_1 = addfile.sha1

    val content = addfile.contentAsString

    val length = addfile.size

    println("Content length " + length)

    val headerLine = Object.HeaderLine(ObjectType.Blob, length)
    println(headerLine)


    //val blob = new Blob(addfile.sha1, "example.txt")

    //println(currentWorkingDirectory/".sgit")

    println("DIRECTORY!: "+ (currentWorkingDirectory/".sgit"/"objects"/sha_1).toString())

    val filePath = currentWorkingDirectory.toString() + "/.sgit/objects/" + sha_1

    println(filePath)

    //val f = File(filePath)
    (currentWorkingDirectory/".sgit"/"objects"/sha_1)
      .createIfNotExists()
      .appendLine(headerLine)
      .appendLine(content)
      .changeExtensionTo(".blob")


    //headerLine should equal ("blob 1234")
//    assert(stack.pop() === 2)
//    assert(stack.pop() === 1)
  }



}