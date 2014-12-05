package easydocs.templates

import easydocs.{Endpoint, Client}
import scala.concurrent.ExecutionContext
import scalatags.Text.all._

class EndpointForm(endpoint: Option[Endpoint], title: String)(implicit ec: ExecutionContext, client: Client) {

  def generateForm(e: Option[Endpoint]) = {

    def getParams: String = e.map(_.params).getOrElse("")

    def genOption(oValue: String, oMatch: Option[String]) = {
      val attrs: Seq[Modifier] = Seq(
        oMatch.flatMap(m => if(m == oValue) Some("selected".attr:="true") else None),
        Some(value:=oValue)
      ).flatten

      option(attrs, oValue)
    }

    def genInput(iType: String, iID: String, iValue: Option[String], iName: String, disableIfValueDefined: Boolean = false) = {
      val disableAttr = if(disableIfValueDefined && iValue.isDefined) Some(disabled:="true") else None
      val valueAttr = iValue.map(v => value:=v)
      val attrs = Seq(
        Some(`type`:=iType),
        Some(`class`:="form-control"),
        Some(id:=iID),
        Some(name:=iName),
        valueAttr,
        disableAttr
      ).flatten
      input(attrs)
    }

    form(role:="form", action:="/api/endpoints?url=/web", "method".attr:="POST",
      div(`class`:="form-group",
        label(`for`:="method-input", "Method:"),
        select(`class`:="form-control", id:="method-input", name:="method", Seq(if(e.isDefined) Some(disabled:="true") else None),
          genOption("GET", e.map(_.method)),
          genOption("POST", e.map(_.method)),
          genOption("PUT", e.map(_.method)),
          genOption("DELETE", e.map(_.method))
        )
      ),

      div(`class`:="form-group",
        label(`for`:="route-input", "Route:"),
        genInput("text", "route-input", e.map(_.route), "route", disableIfValueDefined = true)
      ),

      div(`class`:="form-group",
        label(`for`:="description-input", "Description"),
        genInput("text", "description-input", e.map(_.description), "description")
      ),

      div(`class`:="form-group",
        label(`for`:="contenttype-input", "Content-Type:"),
        select(`class`:="form-control", id:="contenttype-input", name:="contentType", Seq(if(e.isDefined) Some(disabled:="true") else None),
          genOption("application/json", e.map(_.contentType)),
          genOption("application/x-www-form-urlencoded", e.map(_.contentType)),
          genOption("multipart/form-data", e.map(_.contentType)),
          genOption("text/plain", e.map(_.contentType))
        )
      ),

      div(`class`:="form-group",
        label(`for`:="authentication-input", "Authentication:"),
        genInput("text", "authentication-input", e.map(_.authentication), "authentication")
      ),

      div(`class`:="form-group",
        label(`for`:="params-input", "Params:"),
        textarea(`class`:="form-control", id:="params-input", name:="params", rows:="10", getParams)
      ),

      if(e.isDefined){
        Seq(
          input(`type`:="hidden", name:="route", value:=e.get.route),
          input(`type`:="hidden", name:="method", value:=e.get.method),
          input(`type`:="hidden", name:="contentType", value:=e.get.contentType)
        )
      } else { Seq.empty[scalatags.text.Frag] },

      button(`type`:="submit", `class`:="btn btn-default", "Submit")
    )
  }

  def render = for {
    generatedContent <- new Content(h1(title), generateForm(endpoint)).render
  } yield {
    html(
      Header.render,
      generatedContent
    )
  }
}
