import Configuration._

lazy val `tkachuko-blog` = (project in file("."))
  .settings(rootSettings: _*)
  .aggregate(modelsJvm, `db-access`, backend, frontend)

lazy val `db-access` = (project in file("db-access"))
  .settings(dbAccessSettings: _*).dependsOn(modelsJvm, util, `db-client`)

lazy val `db-client` = (project in file("db-client"))
  .settings(modelsSettings: _*).dependsOn(modelsJvm)

lazy val models = (crossProject.crossType(CrossType.Pure) in file("models"))
  .settings(modelsSettings: _*)

lazy val modelsJvm = models.jvm

lazy val modelsJs = models.js
  .settings(assembly := new File(""))

lazy val util = (project in file("util"))
  .settings(utilSettings: _*)

lazy val backend = (project in file("backend"))
  .settings(backendSettings: _*)
  .dependsOn(`db-access`, modelsJvm)
  .settings(
    (resourceGenerators in Compile) <+=
      (fastOptJS in Compile in frontend, packageScalaJSLauncher in Compile in frontend)
        .map((f1, f2) => Seq(f1.data, f2.data)),
    watchSources <++= (watchSources in frontend)
  )

lazy val frontend = (project in file("frontend"))
  .dependsOn(modelsJs)
  .settings(
    commonSettings
      ++ Seq(
      libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % "0.8.0",
        "com.lihaoyi" %%% "scalatags" % "0.4.5",
        "com.lihaoyi" %%% "upickle" % "0.4.1",
        "com.lihaoyi" %%% "utest" % "0.4.0" % "test"
      ),
      testFrameworks += new TestFramework("utest.runner.Framework"),
      scalaJSStage := FastOptStage
    ): _*)
  .disablePlugins(sbtassembly.AssemblyPlugin)
  .enablePlugins(ScalaJSPlugin)

lazy val deploy = TaskKey[Unit]("deploy", "Deploys assembled jar to server")

deploy <<= Tasks.deployWebServer(backend)