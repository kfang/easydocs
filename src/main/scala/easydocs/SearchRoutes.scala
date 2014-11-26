package easydocs

import spray.routing.{Directives, Route}

trait SearchRoutes {
  this: Directives with Services =>

  import system.dispatcher

  val searchRoutes: Route = (get & path("search") & parameters('query)){ query => complete {
    for {
      navigation <- new Navigation().build(esClient)
      searchRes <- esClient.doSearch(query)
    } yield {

      val searchResElems = searchRes.map(endpoint => {
          <div style="background: #ddddff; max-width: 30%; padding-left: 20px">
            <h3><a href={"/endpoints/" + slugify(endpoint.route)}>{endpoint.route}</a></h3>
            <p>{endpoint.description}</p>
          </div>
      })

      <html>
        {header}
        <div id="container-fluid">
          <div class="row">
            {navigation}
            <div class="col-md-10">
              <h1>Search</h1>
              {searchResElems}
            </div>
          </div>
        </div>
      </html>
    }
  }}

}
