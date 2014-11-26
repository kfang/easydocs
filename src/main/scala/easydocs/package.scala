import scala.xml.Elem

package object easydocs {

  def slugify(route: String): String = {
    val s = route.toLowerCase.replaceAll("/", "-")
    if(s.startsWith("-")) s.substring(1) else s
  }

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
      <form role="form" action={"/endpoints/" + slugify(endpoint.route) + "/delete"} method="post">
        <button type="submit" class="btn btn-danger">Delete</button>
      </form>
      <form role="form" action={"/endpoints/" + slugify(endpoint.route) + "/update"} method="get">
        <button type="submit" class="btn btn-info">Update</button>
      </form>
    </div>
  }

}
