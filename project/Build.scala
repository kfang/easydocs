import sbt._
import Keys._
import spray.revolver.RevolverPlugin._

object Build extends Build {

  //configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  lazy val commonDependencies = Seq(
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
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    libraryDependencies ++= commonDependencies
  )

  lazy val jvm = (project in file("jvm"))
    .settings(commonSettings: _*)
    .settings(Revolver.settings: _*)

}