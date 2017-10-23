import java.io.PrintWriter
import java.nio.file.{Files, StandardCopyOption}

import scala.io.Source

enablePlugins(ScalaJSPlugin)

name := "tkachuko-blog"

scalaVersion := "2.12.2"

scalaBinaryVersion := "2.12"

sbtVersion := "0.13.16"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.2",
  "com.lihaoyi" %%% "scalatags" % "0.6.7",
  "com.typesafe.play" %%% "play-json" % "2.6.6",
  "com.lihaoyi" %%% "utest" % "0.6.0" % Test,
  "org.scalatest" %%% "scalatest" % "3.0.4" % Test
)

testFrameworks += new TestFramework("utest.runner.Framework")

scalaJSStage := FastOptStage

mainClass := Some("com.tkachuko.blog.frontend.App")

val githubPages = taskKey[Unit]("Copy index.html and js files to match github pages layout")

githubPages := {
  // 1) Copy compiled prod js
  val js = (fullOptJS in Compile).value.data
  val jsTarget = new File(".", js.getName)
  Files.copy(js.toPath, jsTarget.toPath, StandardCopyOption.REPLACE_EXISTING)

  // 2) Read current index.html
  val indexContent = Source.fromFile("./src/main/resources/index.html")
    .mkString
    .replace("fastopt", "opt")
    .replace("target/scala-2.12/", "")

  // 3) Create prod index.html
  val index = new File(".", "index.html")
  if (index.exists()) index.delete()
  val writer = new PrintWriter(index)
  writer.write(indexContent)
  writer.close()
}