package easydocs

import scala.concurrent.ExecutionContext

case class NavigationItem(
  method: String,
  route: String,
  contentType: String
){

  def shortenedCType: String = {
    "..." + contentType.takeRight(7)
  }
}

class Navigation(implicit ec: ExecutionContext) {

  def build(client: Client) = {
    client.getNavigationItems.map(navItems => {
      <div class="col-md-3" style="background: #dddddd; height: 100%">
        <!-- Search Form -->
        <form class="form-inline" role="form" style="margin-top: 10px; margin-left: 10px" action="/search">
          <input type="text" class="form-control" id="search-input" name="query"></input>
          <button type="submit" class="btn btn-default">Search</button>
        </form>

        <!-- Navigation List -->
        <h1 style="margin-left: 10px">Navigation</h1>
        <ul>
          <li><a href="/">Home</a></li>
          {navItems.map(navItem => {
          <li><a href={"/endpoints/" + slugify(navItem)}>{navItem.route + " => " + navItem.method + " (" + navItem.shortenedCType+ ")"}</a></li>
          })}
          <li><a href="/add">+ add</a></li>
        </ul>


      </div>
    })
  }

}
