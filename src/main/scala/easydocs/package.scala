import scala.xml.Elem

package object easydocs {

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

  def header = {
    <head>
      <link rel="stylesheet" href="/css/bootstrap.min.css"></link>
      <link rel="stylesheet" href="/css/bootstrap-theme.min.css"></link>
      <script src="/js/jquery.min.js"></script>
      <script src="/js/bootstrap.min.js"></script>
    </head>
  }

  def endpointActions(endpoint: Endpoint): Elem = {
    <div class="col-md-1" style="background: #dddddd; height: 100%">
      <h3>Actions:</h3>
      <form role="form" action={"/endpoints/" + slugify(endpoint) + "/delete"} method="post">
        <button type="submit" class="btn btn-danger">Delete</button>
      </form>
      <form role="form" action={"/endpoints/" + slugify(endpoint) + "/update"} method="get">
        <button type="submit" class="btn btn-info">Update</button>
      </form>
    </div>
  }

}
