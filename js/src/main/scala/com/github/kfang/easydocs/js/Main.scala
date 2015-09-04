package com.github.kfang.easydocs.js

import org.scalajs.jquery.jQuery

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp

object Main extends JSApp {

  lazy val content = jQuery("#content")

  def main(): Unit = {
    EasyDocsAPI.getSites().map(endpointsListResponse => {
      content.append(endpointsListResponse.toString)
    })
  }

}
