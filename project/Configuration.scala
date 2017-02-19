import Dependencies._
import sbt.Keys._
import sbt._

object Configuration {

  lazy val commonSettings =
    Seq(
      organization := "tkachuko-blog",
      version := "1.1",
      scalaVersion := Versions.scala,
      scalaBinaryVersion := Versions.scalaBinary,
      sbtVersion := Versions.sbt,
      libraryDependencies ++= Seq(scalaTest, mock),
      parallelExecution in Test := false,
      dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      resolvers ++= Seq(
        "RoundEights" at "http://maven.spikemark.net/roundeights",
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
      )
    )

  lazy val modelsSettings: Seq[Setting[_]] = commonSettings

  lazy val dbAccessSettings: Seq[Setting[_]] = commonSettings :+ {
    libraryDependencies ++= Seq(typesafeConfig, mongo, akkaTestkit, embedMongo)
  }

  lazy val backendSettings: Seq[Setting[_]] = commonSettings :+ {
    libraryDependencies ++= Seq(http, httpTestkit, json)
  } :+ Tasks.runLocally

  lazy val rootSettings: Seq[Setting[_]] = commonSettings

  object Versions {
    val scala = "2.11.8"
    val scalaBinary = "2.11"
    val sbt = "0.13.13"
  }

}

object Dependencies {

  val `akka version` = "2.4.16"
  val `akka http version` = "10.0.3"
  val `mongo version` = "0.12.1"

  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "2.2.4" % Test
  val mock: ModuleID = "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test
  val mongo: ModuleID = "org.reactivemongo" %% "reactivemongo" % `mongo version`
  val embedMongo: ModuleID = "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.3"
  val slf4j: ModuleID = "org.slf4j" % "slf4j-api" % "1.7.21"
  val typesafeConfig: ModuleID = "com.typesafe" % "config" % "1.3.0"
  val http: ModuleID = "com.typesafe.akka" %% "akka-http" % `akka http version`
  val json: ModuleID = "com.typesafe.akka" %% "akka-http-spray-json" % `akka http version`
  val httpTestkit: ModuleID = "com.typesafe.akka" %% "akka-http-testkit" % `akka http version` % Test
  val akkaTestkit: ModuleID = "com.typesafe.akka" %% "akka-testkit" % `akka version` % Test
}