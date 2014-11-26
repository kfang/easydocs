name := "easydoc"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.4"

resolvers := Seq(
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "spray repo" at "http://repo.spray.io"
)

libraryDependencies ++= Seq(
  "io.spray"               %%  "spray-can"              % "1.3.2",
  "io.spray"               %%  "spray-routing"          % "1.3.2",
  "com.typesafe.akka"      %% "akka-actor"              % "2.3.7",
  "com.sksamuel.elastic4s" %% "elastic4s"               % "1.3.3",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.3.3"
)

Revolver.settings