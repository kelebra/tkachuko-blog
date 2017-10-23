import java.io.PrintWriter
import java.nio.file.{Files, Paths, StandardCopyOption}

import scala.collection.JavaConverters._

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

  // 2) Copy everything from resources
  new File("./src/main/resources/")
    .listFiles()
    .foreach(file =>
      Files.copy(file.toPath, new File(".", file.getName).toPath, StandardCopyOption.REPLACE_EXISTING)
    )

  // 3) Replace links in index.html
  val index = new File(".", "index.html")
  val content =
    Files.readAllLines(index.toPath).asScala
      .toList
      .mkString("\n")
      .replaceAll("fastopt", "opt")
      .replaceAll("target/scala-2.12/", "")
  val writer = new PrintWriter(index)
  writer.write(content)
  writer.close()
}