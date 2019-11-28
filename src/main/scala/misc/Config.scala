package misc

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
