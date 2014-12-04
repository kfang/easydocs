package easydocs.templates

import scalatags.Text.all._

object Header {

  def render = {
    head(
      link(rel:="stylesheet", href:="/css/bootstrap.min.css"),
      link(rel:="stylesheet", href:="/css/bootstrap-theme.min.css"),
      script(src:="/js/jquery.min.js"),
      script(src:="/js/bootstrap.min.js")
    )
  }

}
