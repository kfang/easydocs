package easydocs

package object templates {

  def slugify(method: String, route: String, contentType: String) = {
    val s = route.toLowerCase.replaceAll("/", "-")
    val s2 = if (s.startsWith("-")) method.toLowerCase + s else method.toLowerCase + "-" + s
    contentType.split("/").toList match {
      case Nil            => s2
      case x :: Nil       => x + "-" + s2
      case x :: x2 :: xs  => x2 + "-" + s2
    }
  }

  def slugify(e: Endpoint): String = slugify(e.method, e.route, e.contentType)

  def slugify(i: NavigationItem): String = slugify(i.method, i.route, i.contentType)

}
