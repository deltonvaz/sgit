import org.scalatest.FlatSpec
import better.files._
import File._

class InitTest extends FlatSpec {
  "A new git repo" should "create HEAD, index files and object/refs folder" in {
    val f4: File = currentWorkingDirectory
    val base_git: File = currentWorkingDirectory/".sgit/"
//    println(base_git.exists)
//    (currentWorkingDirectory/".sgit/").createIfNotExists()
//    println(f4.contains(base_git))
//    print("Imprimindo documentos: \n"+f4.list.toSeq.foreach(println))

    FileHandler.systemVerify()

    Sgit.init()
    //val dir = "src"/"test"
    //val matches: Iterator[File] = dir.glob("*.{java,scala}")
  }
}
