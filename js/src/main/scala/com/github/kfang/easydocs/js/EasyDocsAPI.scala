package com.github.kfang.easydocs.js

import com.github.kfang.easydocs.models.EZEndpointsListResponse
import org.scalajs.dom.ext.Ajax
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object EasyDocsAPI {

  val BASE = "http://easydocs.zipfworks.com/api/"

  def getSites(): Future[EZEndpointsListResponse] = {
    Ajax.get(url = BASE + "sites").map(xmlHttpRequest => {
      read[EZEndpointsListResponse](xmlHttpRequest.responseText)
    })
  }

  def getTopics(site: String) = {
    Ajax.get(url = BASE + s"sites/$site/topics").map(xmlHttpRequest => {
      println(xmlHttpRequest.responseText)
    })
  }

}


