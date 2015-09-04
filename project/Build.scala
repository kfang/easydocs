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

  lazy val jvmDependencies = Seq(
    "io.spray"               %%  "spray-can"              % "1.3.2",
    "io.spray"               %%  "spray-routing"          % "1.3.2",
    "io.spray"               %%  "spray-json"             % "1.3.1",
    "com.typesafe.akka"      %% "akka-actor"              % "2.3.7",
    "com.typesafe.akka"      %% "akka-slf4j"              % "2.3.8",
    "ch.qos.logback"          % "logback-classic"         % "1.0.7",
    "com.sksamuel.elastic4s" %% "elastic4s"               % "1.3.3",
    "com.scalatags" %% "scalatags" % "0.4.2",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.3.3",
    "com.github.rjeschke" % "txtmark" % "0.11"
  )

  lazy val commonSettings = Seq(
    organization := "com.github.kfang",
    version := "0.0.2-SNAPSHOT",
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
  )

  lazy val jvm = (project in file("jvm"))
    .settings(commonSettings: _*)
    .settings(Revolver.settings: _*)
    .settings(libraryDependencies ++= jvmDependencies)

  lazy val js = (project in file("js"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings({
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0"
    )
  })

}