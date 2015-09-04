package com.github.kfang.easydocs.js

import com.github.kfang.easydocs.js.modules.{SitesListTable, TopNavbar}
import org.scalajs.jquery.jQuery
import org.scalajs.dom._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp

object Main extends JSApp {

  lazy val content = jQuery("body")

  private def renderHomepage(): Unit = for {
    sites <- EasyDocsAPI.getSites()
  } yield {
    content.append(SitesListTable(sites).el)
  }


  private def resetPage(): Unit = {
    content.html("")
    content.append(TopNavbar().el)
  }

  private def changePage(route: Array[String]): Unit = {
    val filtered = route
      .filterNot(x => x == "#" || x == "").toList
      .map(x => if(x.startsWith("#")) x.substring(1) else x)

    resetPage()

    filtered match {
      case Nil  => renderHomepage()
      case _   => println(s"unknown route: $filtered")
    }
  }

  def routeTo(route: String*): Unit = {
    val filtered = route
      .filterNot(x => x == "#" || x == "").toList
      .map(x => if(x.startsWith("#")) x.substring(1) else x)
    window.location.hash = filtered.mkString("/")
  }

  def goBack(): Unit = {
    window.history.back()
  }

  def main(): Unit = {
    window.onhashchange = (e: HashChangeEvent) => {
      changePage(window.location.hash.split('/'))
    }
    jQuery(changePage(window.location.hash.split('/')))
  }

}
