import Dependencies._
import org.scoverage.coveralls.Imports.CoverallsKeys._
import sbt.Keys._
import sbt._

object Configuration {

  lazy val commonSettings =
    Seq(
      organization := "com.tkachuko.blog",
      version := "0.1-SNAPSHOT",
      scalaVersion := Versions.scala,
      scalaBinaryVersion := Versions.scalaBinary,
      sbtVersion := Versions.sbt,
      libraryDependencies ++= Seq(scalaTest),
      parallelExecution in Test := false,
      dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      coverallsToken := Some("BnKklcH3fqZnPMVeBSpNg23t9S0VJPcZG")
    )

  lazy val modelsSettings = commonSettings

  lazy val dbAccessSettings = commonSettings :+ {
    libraryDependencies ++= Seq(typesafeConfig, h2, sorm)
  }

  lazy val backendSettings = commonSettings :+ {
    libraryDependencies ++= Seq(http, testkit)
  }

  lazy val rootSettings = commonSettings

  object Versions {
    val scala = "2.11.7"
    val scalaBinary = "2.11"
    val sbt = "0.13.7"
  }

}

object Dependencies {

  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  val sorm: ModuleID = "org.sorm-framework" % "sorm" % "0.3.19"
  val h2: ModuleID = "com.h2database" % "h2" % "1.3.168" % "test"
  val typesafeConfig: ModuleID = "com.typesafe" % "config" % "1.3.0"
  val http: ModuleID = "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2"
  val testkit: ModuleID = "com.typesafe.akka" %% "akka-http-testkit-experimental" % "2.4.2-RC3"
}