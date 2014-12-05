package easydocs

case class NavigationItem(
  method: String,
  route: String,
  contentType: String
){

  def shortenedCType: String = {
    "..." + contentType.takeRight(7)
  }

}

