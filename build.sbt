ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "octoview"
  )

val playwrightVersion = "1.56.0"
val sttpVersion = "4.0.13"
val upickleVersion = "4.4.1"
val CirceVersion = "0.14.15"

// Note: requires JDK 11+

libraryDependencies ++= Seq(
  "com.microsoft.playwright" % "playwright" % playwrightVersion,
  "com.softwaremill.sttp.client4" %% "core" % sttpVersion,
  "com.softwaremill.sttp.client4" %% "circe" % sttpVersion,
  //"com.softwaremill.sttp.client3" %% "core" % sttpVersion,
  //"com.softwaremill.sttp.client3" %% "asynchttpclient-backend-future" % sttpVersion,
  "com.lihaoyi" %% "ujson" % upickleVersion,
  "com.lihaoyi" %% "upickle" % upickleVersion,
  "io.circe" %% "circe-generic" % CirceVersion, // For automatic case class decoding
  "io.circe" %% "circe-parser" % CirceVersion,  // For manual parsing if needed
  "io.circe" %% "circe-generic-extras" % "0.14.5-RC1" // Note: This version might lag behind core
)
