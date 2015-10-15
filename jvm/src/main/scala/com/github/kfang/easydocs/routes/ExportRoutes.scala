package com.github.kfang.easydocs.routes

import com.sksamuel.elastic4s.ElasticClient
import com.github.kfang.easydocs.AppPackage
import com.github.kfang.easydocs.routes.responses.{ExportEndpointsResponse, ExportSitesResponse}

class ExportRoutes(implicit App: AppPackage) extends ExtendedDirectives(App){
  import App.system.dispatcher
  implicit private val elasticClient: ElasticClient = App.services.elasticClient
  
  
  private val exportSites = (get & path("sites")){ ExportSitesResponse(elasticClient) }
  private val exportEndpoints = (get & path("endpoints")){ ExportEndpointsResponse(elasticClient) }

  val routes= pathPrefix("export"){
    exportSites ~
    exportEndpoints
  }

}
