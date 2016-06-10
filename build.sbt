import Configuration._

lazy val root = (project in file("."))
  .settings(rootSettings: _*)
  .aggregate(modelsJvm, modelsJs, dbAccess, backend)

lazy val dbAccess = (project in file("db-access"))
  .settings(dbAccessSettings: _*).dependsOn(modelsJvm, util)

lazy val models = (crossProject.crossType(CrossType.Pure) in file("models"))
  .settings(modelsSettings: _*)

lazy val modelsJvm = models.jvm

lazy val modelsJs = models.js

lazy val util = (project in file("util"))
  .settings(utilSettings: _*)

lazy val backend = (project in file("backend"))
  .settings(backendSettings: _*)
  .dependsOn(dbAccess, modelsJvm)
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
        "be.doeraene" %%% "scalajs-jquery" % "0.9.0",
        "com.lihaoyi" %%% "utest" % "0.3.1"
      ),
//      jsDependencies ++= Seq(
//        "org.webjars.bower" % "prism" % "1.5.0" / "prism.js"
//      ),
      testFrameworks += new TestFramework("utest.runner.Framework")
    ): _*)
  .enablePlugins(ScalaJSPlugin)