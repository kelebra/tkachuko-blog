import java.awt.Desktop

import Dependencies._
import com.decodified.scalassh.{HostConfig, PasswordLogin}
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys
import com.decodified.scalassh.SshClient
import jassh.SSH.shell

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

object Tasks {

  val runLocally = {

    lazy val runWeb = inputKey[Unit]("Runs web server locally")

    runWeb := {
      (runMain in Compile).fullInput(" com.tkachuko.blog.backend.WebServer 127.0.0.1 9090").evaluated
    }
  }

  def deployWebServer(project: Project) = {

    def deployAndRunJar(jar: File): Unit = {

      val (host, user, password, target) = ("198.211.104.55", "root", readLine("Enter ssh password : "), "~/blog.jar")

      println(s"Stopping running java processes in $host")
      shell(host = host, username = user, password = password) {
        sh =>
          import sh._
          kill(ps().filter(_.cmdline.contains("blog.jar")).map(_.pid))
      }

      println(s"Copying jar to $host as $target")
      SshClient(
        host = host,
        HostConfig(
          login = PasswordLogin(user, password),
          hostName = host,
          hostKeyVerifier = new PromiscuousVerifier())
      ) match {
        case Right(client) => client.upload(jar.getAbsolutePath, target).fold(println, identity[Unit])
        case Left(error) => println(error)
      }

      println(s"Running $target on $host")
      shell(host = host, username = user, password = password) {
        sh =>
          import sh._
          execute(s"nohup java -jar $target 0.0.0.0 80 &")
      }

      println("Deployment done. §Opening blog page...")
      Desktop.getDesktop.browse(new URI("http://tkachuko.info"))
    }

    ((AssemblyKeys.assembly in project) dependsOn (clean in project)) map { file => deployAndRunJar(file) }
  }
}