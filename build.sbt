import Configuration._

lazy val root = (project in file(".")).settings(rootSettings: _*).aggregate(models, dbAccess, backend)

lazy val dbAccess = (project in file("db-access")).settings(dbAccessSettings: _*).dependsOn(models)

lazy val models = (project in file("models")).settings(modelsSettings: _*)

lazy val backend = (project in file("backend")).settings(backendSettings: _*).dependsOn(dbAccess % "compile->compile;test->test")