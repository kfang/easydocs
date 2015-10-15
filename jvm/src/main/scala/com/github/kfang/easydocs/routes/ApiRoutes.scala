package com.github.kfang.easydocs.routes

import com.github.kfang.easydocs.AppPackage
import com.github.kfang.easydocs.utils.CorsSupport
import CorsSupport._

class ApiRoutes(implicit App: AppPackage) extends ExtendedDirectives(App) {

  val routes = cors(pathPrefix("api"){
    new ApiSiteRoutes().routes ~
    new ApiEndpointRoutes().routes ~
    new ExportRoutes().routes
  })

}
