package easydocs

import scala.concurrent.ExecutionContext

class Navigation(implicit ec: ExecutionContext) {

  def build(client: Client) = {
    client.getRouteSlugs.map(routes => {
      <div class="col-md-2" style="background: #dddddd; height: 100%">
        <!-- Search Form -->
        <form class="form-inline" role="form" style="margin-top: 10px; margin-left: 10px" action="/search">
          <input type="text" class="form-control" id="search-input" name="query"></input>
          <button type="submit" class="btn btn-default">Search</button>
        </form>

        <!-- Navigation List -->
        <h1 style="margin-left: 10px">Navigation</h1>
        <ul>
          <li><a href="/">Home</a></li>
          {routes.map(r => <li><a href={"/endpoints/" + r}>{r}</a></li>)}
          <li><a href="/add">+ add</a></li>
        </ul>


      </div>
    })
  }

}
