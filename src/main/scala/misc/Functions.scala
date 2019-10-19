package misc

object Functions {
  val helpMessage : String = """
  Usage:
    Create an empty Sgit repository
    """+ Console.GREEN + "init" + Console.RESET +
  """

  Show the working tree status
    """ + Console.GREEN + "status" + Console.RESET +
  """

  Show changes between commits, commit and working tree, etc
    """ + Console.GREEN + "diff" + Console.RESET +
  """

  Add file contents to the index
    """ + Console.GREEN + "add <filename/filenames or . or regexp>" + Console.RESET +
    """

  Record changes to the repository
    """ + Console.GREEN + "commit <filename/filenames or . or regexp>" + Console.RESET +
  """

  Show commit logs
    """ + Console.GREEN + "log" + Console.RESET +
  """

  Show commit logs started with newest
    """ + Console.GREEN + "log -p" + Console.RESET +
  """

  Show changes overtime
    """ + Console.GREEN + "log -stat" + Console.RESET +
  """

  Create a new branch
    """ + Console.GREEN + "branch <branch name>" + Console.RESET +
  """

  List all existing branches and tags
    """ + Console.GREEN + "branch -av" + Console.RESET +
    """

  Switch branches or restore working tree files
    """ + Console.GREEN + "checkout <branch or tag or commit hash>" + Console.RESET +
  """

  Create tag
    """ + Console.GREEN + "tag <tag name>" + Console.RESET +
    """

  Join two or more development histories together
    """ + Console.GREEN + "merge <branch>" + Console.RESET +
  """

  Reapply commits on top of another base tip
    """ + Console.GREEN + "rebase <branch>" + Console.RESET +
    """

  Make a list of the commits which are about to be rebased. Let the user edit that list before rebasing.
    """ + Console.GREEN + "merge <branch>" + Console.RESET
}
