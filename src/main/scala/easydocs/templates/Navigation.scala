package easydocs.templates

import easydocs.{HeadingSlug, TopicHeading, NavigationItem, Client}
import scala.concurrent.ExecutionContext
import scalatags.Text.all._

class Navigation(client: Client)(implicit ec: ExecutionContext) {

  private def asLink(navItem: NavigationItem) = {
    li(a(href:={"/web/endpoints/" + slugify(navItem)}, s"${navItem.route} => ${navItem.method} (${navItem.shortenedCType})"))
  }

  private def asLink(h: HeadingSlug) = {
    li(a(href:={"/web/endpoints/" + slugify(h.method, h.route, h.cType)}, h.heading))
  }

  private def renderTopicHeadings(topics: List[TopicHeading]) = {
    topics.map(head => {
      li(head.topic, ul(
        for(heading <- head.headings) yield asLink(heading)
      ))
    })
  }

  def render = for {
    topicHeadings <- client.getTopics
    navItems      <- client.getNavigationItems
  } yield {
    div(`class`:="col-md-3", style:="background:#dddddd; height:100%",

      form(`class`:="form-inline", role:="form", style:="margin-top:10px; margin-left: 10px", action:="/search",
        input(`type`:="text", `class`:="form-control", id:="search-input", name:="query"),
        button(`type`:="submit", `class`:="btn btn-default", "Search")
      ),

      h1(style:="margin-left: 10px", "Navigation"),

      ul(
        li(a(href:="/web", "Home")),
        for(item <- navItems) yield asLink(item),
        li(a(href:="/web/endpoints/add", "+ add")),
        renderTopicHeadings(topicHeadings)
      )
    )
  }

}


