package functions

import better.files.File

case class BranchHandler (workingDir : String) {

  /**
    *
    * @return the name of the branch that HEAD points to
    */
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

  /**
    * Create new file that represents the branch and insert lastCommit info
    * @param branchName name of new branch
    * @return a message to see if the branch has been correctly created
    */
  def newBranch(branchName : String) : String = {
    if((File(workingDir)/Constants.SGIT_ROOT/"HEAD").isEmpty) {
      "fatal: Not a valid object name: 'master'."
    }else if(branchName.isEmpty){
      "invalid branch name"
    }else{
      createBranchFile(branchName) match {
        case None => s"branch named $branchName already exists"
        case Some(s) =>
          s.appendLine(getCurrentBranchCommit)
          s"branch $branchName has been created"
      }
    }
  }

  def getCurrentBranchCommit : String = {
    (File(workingDir)/Constants.SGIT_HEADS/getCurrentBranch).lines.head
  }

  /**
    *
    * @param branchName name of the file in refs/head
    * @return the file of the branch
    */
  def createBranchFile(branchName : String) : Option[File] = {
    if ((File(workingDir)/Constants.SGIT_HEADS/branchName).exists)
      None
    else
      Some((File(workingDir)/Constants.SGIT_HEADS/branchName).createFileIfNotExists())
  }

  def getBranches : String = {
    var retVal = ""
    (File(workingDir)/Constants.SGIT_HEADS)
      .list
      .foreach(f => {
        if(f.name == getCurrentBranch){
          retVal+=Console.GREEN + "* " + f.name + Console.RESET + "\t\t" + f.lines.head + "\t"+ CommitHandler(workingDir).getCommitNameFromSHA(f.lines.head)
        } else{
          retVal+=f.name + "\t\t" + f.lines.head + "\t"+ CommitHandler(workingDir).getCommitNameFromSHA(f.lines.head)
        }
        retVal+="\n"
      })
    retVal
  }




}
