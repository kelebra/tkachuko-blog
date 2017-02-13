import sbt.Keys._
import sbt._

object Tasks {

  val runLocally = {

    lazy val runWeb = inputKey[Unit]("Runs web server locally")

    runWeb := {
      (runMain in Compile).fullInput(" com.tkachuko.blog.backend.WebServer 127.0.0.1 9090").evaluated
    }
  }
}