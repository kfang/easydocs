package com.github.kfang.easydocs.js.modules

import com.github.kfang.easydocs.js.Main
import com.github.kfang.easydocs.models.EZSitesListResponse
import org.scalajs.dom.Event
import scalatags.JsDom.all._

case class SitesListTable(sites: EZSitesListResponse) {

  val el = div(
    h3("Sites"),
    table(
      `class`:="table table-striped table-bordered table-hover",
      thead(
        tr(td("Name"), td("ID"))
      ),
      tbody(
        sites.sites.map(site => {
          val row = tr(style:="cursor: pointer", td(site.name), td(site.id)).render
          row.onclick = (e: Event) => Main.routeTo(site.id)
          row
        })
      )
    )
  ).render

}
