import Dependencies._
import sbt.Keys._
import sbt._

object Configuration {

  lazy val commonSettings =
    Seq(
      organization := "com.tkachuko.blog",
      version := "1.1",
      scalaVersion := Versions.scala,
      scalaBinaryVersion := Versions.scalaBinary,
      sbtVersion := Versions.sbt,
      libraryDependencies ++= Seq(scalaTest),
      parallelExecution in Test := false,
      dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      resolvers ++= Seq(
        "RoundEights" at "http://maven.spikemark.net/roundeights",
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
      )
    )

  lazy val modelsSettings = commonSettings

  lazy val dbAccessSettings = commonSettings :+ {
    libraryDependencies ++= Seq(typesafeConfig, mongo, akkaTestkit)
  }

  lazy val utilSettings = commonSettings :+ {
    libraryDependencies ++= Seq(typesafeConfig, embedMongo, slf4j)
  }

  lazy val backendSettings = commonSettings :+ {
    libraryDependencies ++= Seq(http, httpTestkit, json)
  } :+ Tasks.runLocally

  lazy val rootSettings = commonSettings

  object Versions {
    val scala = "2.11.8"
    val scalaBinary = "2.11"
    val sbt = "0.13.7"
  }

}

object Dependencies {

  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  val mongo: ModuleID = "org.reactivemongo" %% "reactivemongo" % "0.11.11"
  val embedMongo = "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.3-SNAPSHOT"
  val slf4j: ModuleID = "org.slf4j" % "slf4j-api" % "1.7.21"
  val typesafeConfig: ModuleID = "com.typesafe" % "config" % "1.3.0"
  val http: ModuleID = "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2"
  val json: ModuleID = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.2"
  val httpTestkit: ModuleID = "com.typesafe.akka" %% "akka-http-testkit-experimental" % "2.4.2-RC3"
  val akkaTestkit: ModuleID = "com.typesafe.akka" %% "akka-testkit" % "2.4.2"
}