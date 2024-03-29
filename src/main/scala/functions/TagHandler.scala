package functions

import better.files.File

case class TagHandler(workingDir : String) {
  /**
    * Create new file that represents the tag and insert current commit info
    * @param tagName name of the new tag
    * @return a message to see if the branch has been correctly created
    */
  def newTag(tagName : String) : String = {
    if((File(workingDir)/Constants.SGIT_ROOT/"HEAD").isEmpty) {
      "fatal: Not a valid object name: 'master'."
    }else if(tagName.isEmpty){
      "invalid tag name"
    }else{
      createTagFile(tagName) match {
        case None => s"tag named $tagName already exists"
        case Some(s) =>
          s.appendLine(BranchHandler(workingDir).getCurrentBranchCommit)
          s"tag $tagName has been created"
      }
    }
  }

  /**
    *
    * @param tagName name of the file in refs/head
    * @return the file of the branch
    */
  def createTagFile(tagName : String) : Option[File] = {
    if ((File(workingDir)/Constants.SGIT_TAGS/tagName).exists)
      None
    else
      Some((File(workingDir)/Constants.SGIT_TAGS/tagName).createFileIfNotExists())
  }

  /**
    *
    * @return tags that have been already added
    */
  def getTags : String = {
    var retVal = ""
    (File(workingDir)/Constants.SGIT_TAGS)
      .list
      .foreach(f => {
        retVal+=f.name+"\n"
      })
    retVal
  }
}
