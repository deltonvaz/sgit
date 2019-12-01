package UI

/**
  *
  * @param mode which mode/function user selected
  * @param files files used in add function
  * @param cmessage commit message
  * @param branchName used as the name of new branch
  * @param tagName used as the name of new tag
  * @param showAll boolean param to show all branch infos
  * @param verbose show all branch infos in a verbose way
  * @param plog show differences between commits
  * @param statLog unsused
  */
case class Config(
     mode: String = "",
     files: Seq[String] = Seq(),
     cmessage: String = "",
     branchName : String = "",
     tagName : String = "",
     showAll : Boolean = false,
     verbose : Boolean = false,
     plog : Boolean = false,
     statLog : Boolean = false
)
