package com.github.kfang.easydocs.js.modules

import org.scalajs.dom
import scalatags.JsDom.all._

case class TopNavbar() {

  private val nav = "nav".tag[dom.html.Element]

  val el =
    nav(
      `class`:="navbar navbar-default",
      div(
        `class`:="container-fluid",
        div(
          `class`:="collapse navbar-collapse",
          ul(
            `class`:="nav navbar-nav",
            li(a(href:="#", "Home"))
          )
        )
      )
    ).render
}
