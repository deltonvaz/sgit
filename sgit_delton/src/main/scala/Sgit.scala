import java.io.FileNotFoundException
import java.security.AccessControlException

import better.files.File
import better.files.File.currentWorkingDirectory

object Sgit {
  def init(): Unit = {
    FileHandler.systemVerify()
    FileHandler.createGitBaseFiles()
  }

  /*
    The method add consists in:
      1- Generate blob file
      2- Add blob file to INDEX (stage)
   */
  def add(param : String): Unit = {

    //Create switch for param (!!!)

    //Check if the file that is being added exists
    if (!FileHandler.fileExists(param)){
      throw new FileNotFoundException(s"$param not found")
    }

    val newFile = currentWorkingDirectory/param

    /*
      TODO Future to create tree with folders
    */
    val rx = """(?<!/)/(?!/)""".r.unanchored
    val containsPath = param match {
      case rx(_*) => true
      case _ => false
    }

    /*
      If the path contains folders git add must create tree files for each folder
      return a list of trees
     */
    if (containsPath) {
      generateTree(newFile)
    }

    //Creating blob file
    val length = newFile.size

    val content = newFile.contentAsString

    //Generate header for blob file
    val headerLine = Object.HeaderLine(ObjectType.Blob, length)

    (currentWorkingDirectory/".sgit"/"objects"/newFile.sha1)
      .createIfNotExists()
      .appendLine(headerLine)
      .appendLine(content)


    //Put file in the INDEX (stage) file
    (currentWorkingDirectory/".sgit/INDEX").appendLine(newFile.sha1 + "\t" + param)

  }

  def getListOfSubDirectories(directoryName: String): Iterator[String] = {

    File(directoryName).listRecursively.filter(_.isDirectory).map(_.name)
//      .listFiles
//      .filter(_.isDirectory)
//      .map(_.getName)
  }

  def load(): Unit = {

  }

  def generateTree(file : File) : Unit = {
    //Cria as trees
    var path = file.parent
    while(path != currentWorkingDirectory) {
      //println("Mostrando pai: "+path.toString())


      path = path.parent
    }

    //println("parent " + path.parent.toString())
  }

  def initialize(): Unit = {
    if(!FileHandler.hasPermission) throw new AccessControlException("Not allowed to initialize Sgit")
  }

  //Main -> initialize() -> load()
  //initialize = verifica as condicoes basicas para a construcao do sistema
  //load carrega em memoria a descricao do sistema na forma de tree-blobs


}

object Main extends App {
  println("Hello from main Sgit")
}
