import DockerKeys._

import sbtdocker.mutable.Dockerfile

import sbtdocker.ImageName

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
  "com.scalatags" %% "scalatags" % "0.4.2",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.3.3",
  "com.github.rjeschke" % "txtmark" % "0.11"
)

Revolver.settings

dockerSettings

docker <<= docker.dependsOn(Keys.`package`.in(Compile, packageBin))

dockerfile in docker := {
  val jarFile = artifactPath.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    // Base image
    from("devbase:5000/java1.8")
    // Add all files on the classpath
    classpath.files.foreach { file =>
      add(file, "/app/")
    }
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Java with the classpath and the main class
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}

imageName in docker := {
  ImageName(
    namespace = Some("devbase:5000"),
    repository = "easydocs",
    tag = sys.env.get("BUILD_NUMBER"))
}


