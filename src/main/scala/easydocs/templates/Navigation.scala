package easydocs.templates

import easydocs.{NavigationItem, Client}
import scala.concurrent.ExecutionContext
import scalatags.Text.all._

class Navigation(client: Client)(implicit ec: ExecutionContext) {

  private def asLink(navItem: NavigationItem) = {
    println(navItem)
    li(a(href:={"/endpoints/" + easydocs.slugify(navItem)}, s"${navItem.route} => ${navItem.method} (${navItem.shortenedCType})"))
  }

  def render = for {
    navItems <- client.getNavigationItems
  } yield {
    div(`class`:="col-md-3", style:="background:#dddddd; height:100%",

      form(`class`:="form-inline", role:="form", style:="margin-top:10px; margin-left: 10px", action:="/search",
        input(`type`:="text", `class`:="form-control", id:="search-input", name:="query"),
        button(`type`:="submit", `class`:="btn btn-default", "Search")
      ),

      h1(style:="margin-left: 10px", "Navigation"),

      ul(
        li(a(href:="/", "Home")),
        for(item <- navItems) yield asLink(item),
        li(a(href:="/add", "+ add"))
      )
    )
  }

}


