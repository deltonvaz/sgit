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
  var folder : File  = _//(workingPath/folderName).createDirectoryIfNotExists()
  var file1 : File  = _//(workingPath/folderName/fileName1).createFileIfNotExists()
  var file2 : File = _//(workingPath/fileName2).createIfNotExists()

  it should "initialize the system before each test"

  before {
    sgit.init()
    folder = (workingPath/folderName).createDirectoryIfNotExists()
    file1 = (workingPath/folderName/fileName1).createFileIfNotExists()
    file2 = (workingPath/fileName2).createIfNotExists()


    assert(file1.isRegularFile)
    assert(file2.isRegularFile)
  }

  behavior of "sgit branch"
  "when there is no commit" should "return error message" in {
    assert(brancHandler.newBranch("alface").equals("fatal: Not a valid object name: 'master'."))
  }

  "when create new branch" should "check if the branch have been well created" in {
    sgit.add(fileName2)
    sgit.commit("a new commit")
    assert(brancHandler.newBranch(branchName).equals(s"branch $branchName has been created"))
  }

  "when create new branch with same name" should "return an error message" in {
    sgit.add(fileName2)
    sgit.commit("a new commit")
    brancHandler.newBranch(branchName)
    assert(brancHandler.newBranch(branchName).equals(s"branch named $branchName already exists"))
  }

  "when create a new non-named branch" should "return error message" in {
    //assert(brancHandler.newBranch("").equals("invalid branch name"))
  }

  "when use -av" should "list all branches and tags" in {
    sgit.add(folderName+"/"+fileName1)
    sgit.commit("a first commit")
    brancHandler.newBranch(branchName)
    sgit.add(fileName2)
    sgit.commit("a second commit")
    assert(brancHandler.getBranches.length.equals(139)) //brute force test
  }

  after {
    if(delFolder) {
      workingPath.deleteOnExit()
      workingPath.delete()
      assert(!workingPath.isDirectory)
    }
  }

}
