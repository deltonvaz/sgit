import better.files.Dsl.{cwd, mkdirs}
import better.files.File
import misc.BranchHandler
import org.scalatest.{BeforeAndAfter, FlatSpec}

class BranchHandlerTest extends FlatSpec with BeforeAndAfter {
  val delFolder = true
  val branchName = "newBranch"
  val workingPath : File = mkdirs(cwd/"testFolder")
  val sgit = Sgit(workingPath.path.toString)
  val brancHandler = BranchHandler(workingPath.path.toString)
  val fileName1 = "name with spaces.txt"
  val fileName2 = "file2.txt"
  val folderName = "folder"
  val folder : File  = (workingPath/folderName).createDirectoryIfNotExists()
  val file1 : File  = (workingPath/folderName/fileName1).createFileIfNotExists()
  val file2 : File = (workingPath/fileName2).createIfNotExists()

  it should "initialize the system before each test"

  before {
    sgit.init()
  }

  behavior of "sgit branch"
  "when there is no commit" should "return error message" in {
    assert(brancHandler.newBranch("alface").equals("fatal: Not a valid object name: 'master'."))
  }

  "when create new branch" should "check if the branch have been well created" in {
    sgit.add(file2.path.toString)
    sgit.commit("a new commit")
    assert(brancHandler.newBranch(branchName).equals(s"branch $branchName has been created"))
  }

  "when create a new non-named branch" should "return error message" in {
    assert(brancHandler.newBranch("").equals("invalid branch name"))
  }

  "when use -av" should "list all branches and tags" in {
    sgit.add(file1.path.toString)
    sgit.commit("a changed commit")
    brancHandler.getBranchesAndTags
  }


  after {
    if(delFolder) {
      workingPath.deleteOnExit()
      workingPath.delete()
      assert(!workingPath.isDirectory)
    }
  }

}
