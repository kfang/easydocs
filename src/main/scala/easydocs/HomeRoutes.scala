package easydocs

import spray.routing.{Directives, Route}

trait HomeRoutes {
  this: Directives with Services =>

  import system.dispatcher

  val homeRoutes: Route = (get & pathEndOrSingleSlash & complete){
    for {
      navigation <- new Navigation().build(esClient)
    } yield {
      <html>
        {header}
        <div id="container-fluid">
          <div class="row">
            {navigation}
            <div class="col-md-10">
              <h1>Home</h1>
            </div>
          </div>
        </div>
      </html>
    }
  }

}
