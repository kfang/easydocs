import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin.autoImport._
import sbt._
import Keys._
import spray.revolver.RevolverPlugin._

object Build extends Build {

  //configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  lazy val commonSettings = Seq(
    organization := "com.github.kfang",
    version := "0.0.2-SNAPSHOT",
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
  )

  lazy val serverSettings = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka"      %% "akka-http-spray-json-experimental"  % "2.0-M1",
      "com.typesafe.akka"      %% "akka-http-experimental"  % "2.0-M1",
      "com.typesafe.akka"      %% "akka-slf4j"              % "2.3.8",
      "ch.qos.logback"          % "logback-classic"         % "1.0.7",
      "com.sksamuel.elastic4s" %% "elastic4s"               % "1.3.3",
      "com.sksamuel.elastic4s" %% "elastic4s-jackson"       % "1.7.4",
      "com.scalatags"          %% "scalatags"               % "0.4.2",
      "com.github.rjeschke"     % "txtmark"                 % "0.11"
    )
  ) ++ Revolver.settings.settings

  lazy val clientSettings = Seq(
    libraryDependencies ++= Seq(
      "com.lihaoyi"  %%% "upickle"        % "0.3.6",
      "com.lihaoyi"  %%% "scalatags"      % "0.5.2",
      "be.doeraene"  %%% "scalajs-jquery" % "0.8.0"
    ),
    skip in packageJSDependencies := false
  )




  lazy val shared = (crossProject in file("."))
    .settings(commonSettings: _*)
    .jvmSettings(serverSettings: _*)
    .jsSettings(clientSettings: _*)

  lazy val sharedJs = shared.js

  lazy val sharedJvm = shared.jvm
}