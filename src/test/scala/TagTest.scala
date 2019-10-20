import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import misc.TagHandler
import org.scalatest.{BeforeAndAfter, FlatSpec}

class TagTest extends FlatSpec with BeforeAndAfter {
  val delFolder = true
  var tagName = "newTag"
  val workingPath : File = mkdirs(cwd/"testFolder")
  val sgit = Sgit(workingPath.path.toString)
  val tagHandler = TagHandler(workingPath.path.toString)
  val fileName1 = "name with spaces.txt"
  val fileName2 = "file2.txt"
  val folderName = "folder"
  var folder : File  = _
  var file1 : File  = _
  var file2 : File = _

  it should "initialize the system before each test"

  before {
    sgit.init()
    folder = (workingPath/folderName).createDirectoryIfNotExists()
    file1 = (workingPath/folderName/fileName1).createFileIfNotExists()
    file2 = (workingPath/fileName2).createIfNotExists()
    tagName = "newTag"

    assert(file1.isRegularFile)
    assert(file2.isRegularFile)
  }

  behavior of "sgit tag"

  "when there is no commit" should "return error message" in {
    assert(tagHandler.newTag("alface").equals("fatal: Not a valid object name: 'master'."))
  }


  //"when create new tag" should "check if the tag have been well created" in {
  ignore should "check if the tag have been well created" in {
    sgit.add(fileName2)
    sgit.commit("a new commit")
    assert(tagHandler.newTag(tagName).equals(s"tag $tagName has been created"))
  }

  //"when create duplicated tag" should "return message" in {
  ignore should "return message" in {
    sgit.add(fileName2)
    sgit.commit("a new commit")
    tagHandler.newTag(tagName)
    assert(tagHandler.newTag(tagName).equals(s"tag named $tagName already exists"))
  }

  //"when create a new non-named tag" should "return error message" in {
  ignore should "return error message for invalid name" in {
      //assert(tagHandler.newTag("").equals("invalid tag name"))
  }


  after {
    if(delFolder) {
      workingPath.deleteOnExit()
      workingPath.delete()
      assert(!workingPath.isDirectory)
    }
  }

}
