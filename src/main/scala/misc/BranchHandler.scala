package misc

import better.files.File

case class BranchHandler (workingDir : String) {
  def getCurrentBranch: String = {
    if((File(workingDir)/Constants.SGIT_ROOT/"HEAD").isEmpty){
      "master"
    }else{
      val headCommit = (File(workingDir)/Constants.SGIT_ROOT/"HEAD").lines.head
      var retVal = ""
      headCommit match {
        case Constants.BRANCH_PATTERN(a, b) => {
          retVal = b
        }
        case _ => println(Console.RED + "error ocurred while loading branch" + Console.RESET)
      }
      retVal
    }

  }
}
