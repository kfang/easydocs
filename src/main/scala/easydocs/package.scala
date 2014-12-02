import scala.xml.Elem

package object easydocs {

  def slugify(method: String, route: String) = {
    val s = route.toLowerCase.replaceAll("/", "-")
    if (s.startsWith("-")) method.toLowerCase + s else method.toLowerCase + "-" + s
  }

  def slugify(e: Endpoint): String = slugify(e.method, e.route)

  def header = {
    <head>
      <link rel="stylesheet" href="/css/bootstrap.min.css"></link>
      <link rel="stylesheet" href="/css/bootstrap-theme.min.css"></link>
      <script src="/js/jquery.min.js"></script>
      <script src="/js/bootstrap.min.js"></script>
    </head>
  }

  def endpointActions(endpoint: Endpoint): Elem = {
    <div class="col-md-2" style="background: #dddddd; height: 100%">
      <h1>Actions:</h1>
      <form role="form" action={"/endpoints/" + slugify(endpoint) + "/delete"} method="post">
        <button type="submit" class="btn btn-danger">Delete</button>
      </form>
      <form role="form" action={"/endpoints/" + slugify(endpoint) + "/update"} method="get">
        <button type="submit" class="btn btn-info">Update</button>
      </form>
    </div>
  }

}
