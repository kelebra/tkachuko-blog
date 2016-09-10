package com.tkachuko.blog.frontend.markdown

trait HTML {

  def `quoted post` = "<p>\n    For those of you who is as impatient as I am and would like to dive into source code on your\n    own please follow <a href=\"https://github.com/kelebra/tkachuko-blog\">this link</a> which\n    will take you directly to my github repository. Please use master for latest version.\n    <br/>\n    For the ones who would like to get step by step guidance, you are welcome. Let's shape what\n    we would like to achieve: a simple blog which will load posts from database. For simplicity\n    of this very demo we are going to store posts as plain html. On the startup we are going to\n    load them in chronological order and display on a single page.\n    <br/>\n    Let's define our technological stack:\n<ul>\n    <li>Scala (as main programming language)</li>\n    <li>Sbt (to build our project)</li>\n    <li>Akka-http (for rest API and http interaction)</li>\n    <li>skinny-framework (for database interaction)</li>\n    <li>postgres and h2 (databases for production and tests)</li>\n</ul>\n<h4>Project layout and build</h4>\nAs we are going to use sbt we will have to follow its (or better to say maven)\n<a href=\"http://www.scala-sbt.org/0.13/docs/Directories.html\">project structure</a>.\nThe difference between sbt and maven is possibility to programmatically describe subprojects,\ndependencies and everything else you might need for your project build. Let's start defining our\nmodules which are going to split our codebase into some logical pieces. Below you can find\n<i>build.sbt</i> file defined in the root folder of the project:\n<pre><code class=\"language-scala\">\nimport Configuration._\nlazy val root = (project in file(\".\")).settings(rootSettings: _*).aggregate(models, dbAccess, backend)\nlazy val dbAccess = (project in file(\"db-access\")).settings(dbAccessSettings: _*).dependsOn(models)\nlazy val models = (project in file(\"models\")).settings(modelsSettings: _*)\nlazy val backend = (project in file(\"backend\")).settings(backendSettings: _*).dependsOn(dbAccess)\n</code></pre>\nAs you can see here we decided to define our project settings programmatically in the object\n<i>Configuration.scala</i>. This file is located in <i>project</i> folder:\n<pre><code class=\"language-scala\">\nimport Dependencies._\nimport sbt.Keys._\nimport sbt._\n\nobject Configuration {\n\n  lazy val commonSettings =\n    Seq(\n       organization := \"com.tkachuko.blog\",\n       version := \"1.0\",\n       scalaVersion := Versions.scala,\n       scalaBinaryVersion := Versions.scalaBinary,\n       sbtVersion := Versions.sbt,\n       libraryDependencies ++= Seq(scalaTest),\n       parallelExecution in Test := false,\n       dependencyOverrides += \"org.scala-lang\" % \"scala-compiler\" % scalaVersion.value,\n    )\n\n  lazy val modelsSettings = commonSettings\n\n  lazy val dbAccessSettings = commonSettings :+ {\n    libraryDependencies ++= Seq(typesafeConfig, h2, orm)\n  }\n\n  lazy val backendSettings = commonSettings :+ {\n    libraryDependencies ++= Seq(http, testkit, json, h2, postgres)\n  }\n\n  lazy val rootSettings = commonSettings\n\n  object Versions {\n    val scala = \"2.11.7\"\n    val scalaBinary = \"2.11\"\n    val sbt = \"0.13.7\"\n  }\n\n}\n\nobject Dependencies {\n\n  val scalaTest: ModuleID = \"org.scalatest\" %% \"scalatest\" % \"2.2.4\" % \"test\"\n  val orm: ModuleID = \"org.skinny-framework\" %% \"skinny-orm\" % \"2.0.7\"\n  val postgres = \"org.postgresql\" % \"postgresql\" % \"9.4.1208\"\n  val h2: ModuleID = \"com.h2database\" % \"h2\" % \"1.3.168\" % \"test\"\n  val typesafeConfig: ModuleID = \"com.typesafe\" % \"config\" % \"1.3.0\"\n  val http: ModuleID = \"com.typesafe.akka\" %% \"akka-http-experimental\" % \"2.4.2\"\n  val json: ModuleID = \"com.typesafe.akka\" %% \"akka-http-spray-json-experimental\" % \"2.4.2\"\n  val testkit: ModuleID = \"com.typesafe.akka\" %% \"akka-http-testkit-experimental\" % \"2.4.2-RC3\"\n}\n                            </code></pre>\nWe try to separate responsibilities in our build defining every part of build configuration in\na separate object. That is the main reason objects <i>Configuration</i>, <i>Dependencies</i> and\n<i>Versions</i> were introduced. As a result our multi-project definition (<i>build.sbt</i>) is\nnot aware of particular settings for any specific sub-project and is clean and readable.\n<h4>Domain models</h4>\nWe want to keep our domain model as simple as possible so we are going to represent our blog\npost in the following way:\n<pre><code class=\"language-scala\">\npackage object models {\n\n  case class Post(id: Long = System.currentTimeMillis(), title: String, content: String)\n}\n                            </code></pre>\nMillis is more than enough for our domestic blog (unless you are going to post multiple posts in\none millisecond).\n<h4>Database interaction and ORM</h4>\nNow let's define our <i>db-access</i> module and let's define our requirements with this very\nsimple test <i>DatabaseSpec.scala</i>:\n<pre><code class=\"language-scala\">\nimport com.tkachuko.blog.models.Post\nimport org.h2.tools.Server\nimport org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}\n\nclass DatabaseSpec extends WordSpec with Matchers with BeforeAndAfterAll {\n\n  val id = System.currentTimeMillis()\n\n  \"Database\" should {\n\n    \"retrieve persisted record by id\" in {\n      Database.Posts.findById(id) should be('defined)\n    }\n  }\n\n  override protected def beforeAll(): Unit = {\n    Server.createTcpServer(\"-tcpAllowOthers\").start()\n    Database.initialize()\n    Database.save(Post(id = id, title = \"title\", content = \"content\"))\n  }\n}\n                            </code></pre>\nThis test starts in-memory h2 database and first of all tries to save an entity and after that\ntries to verify that it exists. Now, let's implement <i>Database</i> class using <i>skinny-orm</i>:\n<pre><code class=\"language-scala\">\nimport com.tkachuko.blog.models.Post\nimport com.typesafe.config.ConfigFactory\nimport scalikejdbc.{AutoSession, WrappedResultSet, _}\nimport skinny.DBSettings\nimport skinny.orm.{Alias, SkinnyCRUDMapper}\n\npackage object db {\n\n  val config = ConfigFactory.load(\"application.conf\")\n  val init = config.getBoolean(\"development.init\")\n\n  object Database {\n\n    implicit val session = AutoSession\n\n    def initialize(): Unit = {\n      DBSettings.initialize()\n      if (init) {\n        sql\"drop table if exists POSTS;\".execute().apply()\n        sql\"create table POSTS (id serial, title varchar(50), content varchar(100000));\".execute().apply()\n      }\n    }\n\n    object Posts extends SkinnyCRUDMapper[Post] {\n\n      override def defaultAlias: Alias[Post] = createAlias(\"p\")\n\n      override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Post]): Post =\n        Post(rs.long(n.id), rs.string(n.title), rs.string(n.content))\n\n      override def tableName: String = \"POSTS\"\n    }\n\n    def save(post: Post) =\n      sql\"insert into POSTS (id, title, content) values(${post.id}, ${post.title}, ${post.content});\".execute().apply()\n  }\n\n}\n                            </code></pre>\nAs you can see we are using plain SQL here to create required table and query it. Object <i>Posts</i>\nrepresents table mapping defining transformation (method <i>extract</i>) from raw table row\nalong with table tame itself (method <i>tableName</i>). Pretty much simple, right? Also,\nas you can see <i>skinny</i> requires <i>application.conf</i> file to be present in the classpath.\nBelow you can find configuration required for postgres and h2 respectively:\n<pre><code class=\"language-javascript\">\ndevelopment {\n  db {\n    default {\n      driver = \"org.postgresql.Driver\"\n      url = \"jdbc:postgresql://!host!:!port!/!db-name!\"\n      user = \"###\"\n      password = \"###\"\n      poolInitialSize = 2\n      poolMaxSize = 10\n    }\n  }\n  init = false\n}\n                            </code></pre>\n<pre><code class=\"language-javascript\">\ndevelopment {\n  db {\n    default {\n      driver = \"org.h2.Driver\"\n      url = \"jdbc:h2:mem:example\"\n      user = \"sa\"\n      password = \"sa\"\n      poolInitialSize = 2\n      poolMaxSize = 10\n    }\n  }\n  init = true\n}\n                            </code></pre>\n<h4>HTTP Server</h4>\nNow let's define our web server and routes (module <i>backend</i>). And again let's start from\nsimple spec (<i>RoutesSpec.scala</i>):\n<pre><code class=\"language-scala\">\nimport akka.http.scaladsl.model.StatusCodes\nimport akka.http.scaladsl.testkit.ScalatestRouteTest\nimport com.tkachuko.blog.backend.WebServer.routes\nimport com.tkachuko.blog.backend.static.StaticDataResolver._\nimport com.tkachuko.blog.db.Database\nimport com.tkachuko.blog.models.{Post => BlogPost}\nimport org.h2.tools.Server\nimport org.scalatest.{Matchers, WordSpec}\n\nclass RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {\n\n  \"Web server\" should {\n\n    \"return homepage for GET request to the root path\" in {\n      Get() ~> routes ~> check {\n        status === StatusCodes.Success\n        responseAs[String] should not be empty\n      }\n    }\n\n    \"return static resource for GET request to the /pages/css/index.css\" in {\n      Get(s\"/$resourcePrefix/css/index.css\") ~> routes ~> check {\n        status === StatusCodes.Success\n        responseAs[String] should not be empty\n      }\n    }\n\n    \"return all posts as json for GET to the /posts\" in {\n      Get(s\"/$posts\") ~> routes ~> check {\n        status === StatusCodes.Success\n        responseAs[String] should not be empty\n      }\n    }\n  }\n\n  override protected def beforeAll(): Unit = {\n    Server.createTcpServer(\"-tcpAllowOthers\").start()\n    Database.initialize()\n    Database.save(BlogPost(1, \"title\", \"content\"))\n    Database.save(BlogPost(2, \"title other\", \"content\"))\n  }\n}\n                            </code></pre>\nSo requirements to our web server are pretty much simple:\n<ul>\n    <li>Return homepage</li>\n    <li>Return static resources</li>\n    <li>Return all blog posts</li>\n</ul>\nwhich are reflected in these test cases. Before jumping into routes definition let's provide\njson serialization for our domain model:\n<pre><code class=\"language-scala\">\nimport com.tkachuko.blog.models.Post\nimport spray.json._\n\nobject PostJsonSupport extends DefaultJsonProtocol {\n\n  implicit val jsonFormat = jsonFormat3(Post)\n}\n                            </code></pre>\nNow having everything in place, let's define routes for our web server:\n<pre><code class=\"language-scala\">\nimport akka.actor.ActorSystem\nimport akka.http.scaladsl.Http\nimport akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._\nimport akka.http.scaladsl.server.Directives._\nimport akka.stream.ActorMaterializer\nimport com.tkachuko.blog.backend.json.PostJsonSupport._\nimport com.tkachuko.blog.backend.static.StaticDataResolver._\nimport com.tkachuko.blog.db.Database\nimport spray.json._\n\nobject WebServer {\n\n  implicit val system = ActorSystem(\"tkachuko-system\")\n  implicit val mat = ActorMaterializer()\n  implicit val ec = system.dispatcher\n\n  val routes =\n    get {\n      pathSingleSlash {\n        homePage\n      } ~\n      path(resourcePrefix / Rest) { resource =>\n        resource.asWebResource\n      } ~\n      path(blog) {\n        blogPage\n      } ~\n      path(posts) {\n        complete(Database.Posts.findAllModels().toJson)\n      }\n    }\n\n    def main(args: Array[String]): Unit = {\n      val binding = Http().bindAndHandle(routes, args(0), args(1).toInt)\n\n      Database.initialize()\n\n      binding.onFailure {\n        case e: Exception =>\n          println(e)\n          system.terminate()\n    }\n  }\n}                           </code></pre>\nHere we are using simplest capabilities of akka route dsl defining GET method routes for\nfour different URLs. Also, as you probably guessed, we are going to run <i>WebServer.scala</i>\npassing host and port parameters. To complete the picture, find implicits used below:\n<pre><code class=\"language-scala\">\nimport akka.http.scaladsl.server.Directives._\n\nobject StaticDataResolver {\n\n  val resourcePrefix = \"pages\"\n\n  val blog = \"blog\"\n\n  val posts = \"posts\"\n\n  val homePage = \"index.html\".asWebResource\n\n  val blogPage = \"blog.html\".asWebResource\n\n  implicit class WebResource(val path: String) extends AnyVal {\n\n    def asWebResource = getFromResource(s\"$resourcePrefix/$path\")\n  }\n\n}\n                            </code></pre>\n<h4>UI</h4>\nWe are going to send a request to server on page load to load all posts using jquery:\n<pre><code class=\"language-javascript\">\nvar parent = $('.posts');\n$.get(\"/posts\", function(data) {\n  $.each(data, function(index, value){\n    var html = $.parseHTML(value.content);\n    parent.append(html);\n  })\n});\n                            </code></pre>\n<h4>Conclusion</h4>\nHope this post was useful and would server as very simple example of scala world capabilities.\nNext I am going to write about admin creation, post builder or performance testing of this blog.\n</p>"

  def `unquoted post` =
    """<p>
      |    For those of you who is as impatient as I am and would like to dive into source code on your
      |    own please follow <a href="https://github.com/kelebra/tkachuko-blog">this link</a> which
      |    will take you directly to my github repository. Please use master for latest version.
      |    <br/>
      |    For the ones who would like to get step by step guidance, you are welcome. Let's shape what
      |    we would like to achieve: a simple blog which will load posts from database. For simplicity
      |    of this very demo we are going to store posts as plain html. On the startup we are going to
      |    load them in chronological order and display on a single page.
      |    <br/>
      |    Let's define our technological stack:
      |<ul>
      |    <li>Scala (as main programming language)</li>
      |    <li>Sbt (to build our project)</li>
      |    <li>Akka-http (for rest API and http interaction)</li>
      |    <li>skinny-framework (for database interaction)</li>
      |    <li>postgres and h2 (databases for production and tests)</li>
      |</ul>
      |<h4>Project layout and build</h4>
      |As we are going to use sbt we will have to follow its (or better to say maven)
      |<a href="http://www.scala-sbt.org/0.13/docs/Directories.html">project structure</a>.
      |The difference between sbt and maven is possibility to programmatically describe subprojects,
      |dependencies and everything else you might need for your project build. Let's start defining our
      |modules which are going to split our codebase into some logical pieces. Below you can find
      |<i>build.sbt</i> file defined in the root folder of the project:
      |<pre><code class="language-scala">
      |import Configuration._
      |lazy val root = (project in file(".")).settings(rootSettings: _*).aggregate(models, dbAccess, backend)
      |lazy val dbAccess = (project in file("db-access")).settings(dbAccessSettings: _*).dependsOn(models)
      |lazy val models = (project in file("models")).settings(modelsSettings: _*)
      |lazy val backend = (project in file("backend")).settings(backendSettings: _*).dependsOn(dbAccess)
      |</code></pre>
      |As you can see here we decided to define our project settings programmatically in the object
      |<i>Configuration.scala</i>. This file is located in <i>project</i> folder:
      |<pre><code class="language-scala">
      |import Dependencies._
      |import sbt.Keys._
      |import sbt._
      |
      |object Configuration {
      |
      |  lazy val commonSettings =
      |    Seq(
      |       organization := "com.tkachuko.blog",
      |       version := "1.0",
      |       scalaVersion := Versions.scala,
      |       scalaBinaryVersion := Versions.scalaBinary,
      |       sbtVersion := Versions.sbt,
      |       libraryDependencies ++= Seq(scalaTest),
      |       parallelExecution in Test := false,
      |       dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      |    )
      |
      |  lazy val modelsSettings = commonSettings
      |
      |  lazy val dbAccessSettings = commonSettings :+ {
      |    libraryDependencies ++= Seq(typesafeConfig, h2, orm)
      |  }
      |
      |  lazy val backendSettings = commonSettings :+ {
      |    libraryDependencies ++= Seq(http, testkit, json, h2, postgres)
      |  }
      |
      |  lazy val rootSettings = commonSettings
      |
      |  object Versions {
      |    val scala = "2.11.7"
      |    val scalaBinary = "2.11"
      |    val sbt = "0.13.7"
      |  }
      |
      |}
      |
      |object Dependencies {
      |
      |  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
      |  val orm: ModuleID = "org.skinny-framework" %% "skinny-orm" % "2.0.7"
      |  val postgres = "org.postgresql" % "postgresql" % "9.4.1208"
      |  val h2: ModuleID = "com.h2database" % "h2" % "1.3.168" % "test"
      |  val typesafeConfig: ModuleID = "com.typesafe" % "config" % "1.3.0"
      |  val http: ModuleID = "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2"
      |  val json: ModuleID = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.2"
      |  val testkit: ModuleID = "com.typesafe.akka" %% "akka-http-testkit-experimental" % "2.4.2-RC3"
      |}
      |                            </code></pre>
      |We try to separate responsibilities in our build defining every part of build configuration in
      |a separate object. That is the main reason objects <i>Configuration</i>, <i>Dependencies</i> and
      |<i>Versions</i> were introduced. As a result our multi-project definition (<i>build.sbt</i>) is
      |not aware of particular settings for any specific sub-project and is clean and readable.
      |<h4>Domain models</h4>
      |We want to keep our domain model as simple as possible so we are going to represent our blog
      |post in the following way:
      |<pre><code class="language-scala">
      |package object models {
      |
      |  case class Post(id: Long = System.currentTimeMillis(), title: String, content: String)
      |}
      |                            </code></pre>
      |Millis is more than enough for our domestic blog (unless you are going to post multiple posts in
      |one millisecond).
      |<h4>Database interaction and ORM</h4>
      |Now let's define our <i>db-access</i> module and let's define our requirements with this very
      |simple test <i>DatabaseSpec.scala</i>:
      |<pre><code class="language-scala">
      |import com.tkachuko.blog.models.Post
      |import org.h2.tools.Server
      |import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
      |
      |class DatabaseSpec extends WordSpec with Matchers with BeforeAndAfterAll {
      |
      |  val id = System.currentTimeMillis()
      |
      |  "Database" should {
      |
      |    "retrieve persisted record by id" in {
      |      Database.Posts.findById(id) should be('defined)
      |    }
      |  }
      |
      |  override protected def beforeAll(): Unit = {
      |    Server.createTcpServer("-tcpAllowOthers").start()
      |    Database.initialize()
      |    Database.save(Post(id = id, title = "title", content = "content"))
      |  }
      |}
      |                            </code></pre>
      |This test starts in-memory h2 database and first of all tries to save an entity and after that
      |tries to verify that it exists. Now, let's implement <i>Database</i> class using <i>skinny-orm</i>:
      |<pre><code class="language-scala">
      |import com.tkachuko.blog.models.Post
      |import com.typesafe.config.ConfigFactory
      |import scalikejdbc.{AutoSession, WrappedResultSet, _}
      |import skinny.DBSettings
      |import skinny.orm.{Alias, SkinnyCRUDMapper}
      |
      |package object db {
      |
      |  val config = ConfigFactory.load("application.conf")
      |  val init = config.getBoolean("development.init")
      |
      |  object Database {
      |
      |    implicit val session = AutoSession
      |
      |    def initialize(): Unit = {
      |      DBSettings.initialize()
      |      if (init) {
      |        sql"drop table if exists POSTS;".execute().apply()
      |        sql"create table POSTS (id serial, title varchar(50), content varchar(100000));".execute().apply()
      |      }
      |    }
      |
      |    object Posts extends SkinnyCRUDMapper[Post] {
      |
      |      override def defaultAlias: Alias[Post] = createAlias("p")
      |
      |      override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Post]): Post =
      |        Post(rs.long(n.id), rs.string(n.title), rs.string(n.content))
      |
      |      override def tableName: String = "POSTS"
      |    }
      |
      |    def save(post: Post) =
      |      sql"insert into POSTS (id, title, content) values(${post.id}, ${post.title}, ${post.content});".execute().apply()
      |  }
      |
      |}
      |                            </code></pre>
      |As you can see we are using plain SQL here to create required table and query it. Object <i>Posts</i>
      |represents table mapping defining transformation (method <i>extract</i>) from raw table row
      |along with table tame itself (method <i>tableName</i>). Pretty much simple, right? Also,
      |as you can see <i>skinny</i> requires <i>application.conf</i> file to be present in the classpath.
      |Below you can find configuration required for postgres and h2 respectively:
      |<pre><code class="language-javascript">
      |development {
      |  db {
      |    default {
      |      driver = "org.postgresql.Driver"
      |      url = "jdbc:postgresql://!host!:!port!/!db-name!"
      |      user = "###"
      |      password = "###"
      |      poolInitialSize = 2
      |      poolMaxSize = 10
      |    }
      |  }
      |  init = false
      |}
      |                            </code></pre>
      |<pre><code class="language-javascript">
      |development {
      |  db {
      |    default {
      |      driver = "org.h2.Driver"
      |      url = "jdbc:h2:mem:example"
      |      user = "sa"
      |      password = "sa"
      |      poolInitialSize = 2
      |      poolMaxSize = 10
      |    }
      |  }
      |  init = true
      |}
      |                            </code></pre>
      |<h4>HTTP Server</h4>
      |Now let's define our web server and routes (module <i>backend</i>). And again let's start from
      |simple spec (<i>RoutesSpec.scala</i>):
      |<pre><code class="language-scala">
      |import akka.http.scaladsl.model.StatusCodes
      |import akka.http.scaladsl.testkit.ScalatestRouteTest
      |import com.tkachuko.blog.backend.WebServer.routes
      |import com.tkachuko.blog.backend.static.StaticDataResolver._
      |import com.tkachuko.blog.db.Database
      |import com.tkachuko.blog.models.{Post => BlogPost}
      |import org.h2.tools.Server
      |import org.scalatest.{Matchers, WordSpec}
      |
      |class RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {
      |
      |  "Web server" should {
      |
      |    "return homepage for GET request to the root path" in {
      |      Get() ~> routes ~> check {
      |        status === StatusCodes.Success
      |        responseAs[String] should not be empty
      |      }
      |    }
      |
      |    "return static resource for GET request to the /pages/css/index.css" in {
      |      Get(s"/$resourcePrefix/css/index.css") ~> routes ~> check {
      |        status === StatusCodes.Success
      |        responseAs[String] should not be empty
      |      }
      |    }
      |
      |    "return all posts as json for GET to the /posts" in {
      |      Get(s"/$posts") ~> routes ~> check {
      |        status === StatusCodes.Success
      |        responseAs[String] should not be empty
      |      }
      |    }
      |  }
      |
      |  override protected def beforeAll(): Unit = {
      |    Server.createTcpServer("-tcpAllowOthers").start()
      |    Database.initialize()
      |    Database.save(BlogPost(1, "title", "content"))
      |    Database.save(BlogPost(2, "title other", "content"))
      |  }
      |}
      |                            </code></pre>
      |So requirements to our web server are pretty much simple:
      |<ul>
      |    <li>Return homepage</li>
      |    <li>Return static resources</li>
      |    <li>Return all blog posts</li>
      |</ul>
      |which are reflected in these test cases. Before jumping into routes definition let's provide
      |json serialization for our domain model:
      |<pre><code class="language-scala">
      |import com.tkachuko.blog.models.Post
      |import spray.json._
      |
      |object PostJsonSupport extends DefaultJsonProtocol {
      |
      |  implicit val jsonFormat = jsonFormat3(Post)
      |}
      |                            </code></pre>
      |Now having everything in place, let's define routes for our web server:
      |<pre><code class="language-scala">
      |import akka.actor.ActorSystem
      |import akka.http.scaladsl.Http
      |import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
      |import akka.http.scaladsl.server.Directives._
      |import akka.stream.ActorMaterializer
      |import com.tkachuko.blog.backend.json.PostJsonSupport._
      |import com.tkachuko.blog.backend.static.StaticDataResolver._
      |import com.tkachuko.blog.db.Database
      |import spray.json._
      |
      |object WebServer {
      |
      |  implicit val system = ActorSystem("tkachuko-system")
      |  implicit val mat = ActorMaterializer()
      |  implicit val ec = system.dispatcher
      |
      |  val routes =
      |    get {
      |      pathSingleSlash {
      |        homePage
      |      } ~
      |      path(resourcePrefix / Rest) { resource =>
      |        resource.asWebResource
      |      } ~
      |      path(blog) {
      |        blogPage
      |      } ~
      |      path(posts) {
      |        complete(Database.Posts.findAllModels().toJson)
      |      }
      |    }
      |
      |    def main(args: Array[String]): Unit = {
      |      val binding = Http().bindAndHandle(routes, args(0), args(1).toInt)
      |
      |      Database.initialize()
      |
      |      binding.onFailure {
      |        case e: Exception =>
      |          println(e)
      |          system.terminate()
      |    }
      |  }
      |}                           </code></pre>
      |Here we are using simplest capabilities of akka route dsl defining GET method routes for
      |four different URLs. Also, as you probably guessed, we are going to run <i>WebServer.scala</i>
      |passing host and port parameters. To complete the picture, find implicits used below:
      |<pre><code class="language-scala">
      |import akka.http.scaladsl.server.Directives._
      |
      |object StaticDataResolver {
      |
      |  val resourcePrefix = "pages"
      |
      |  val blog = "blog"
      |
      |  val posts = "posts"
      |
      |  val homePage = "index.html".asWebResource
      |
      |  val blogPage = "blog.html".asWebResource
      |
      |  implicit class WebResource(val path: String) extends AnyVal {
      |
      |    def asWebResource = getFromResource(s"$resourcePrefix/$path")
      |  }
      |
      |}
      |                            </code></pre>
      |<h4>UI</h4>
      |We are going to send a request to server on page load to load all posts using jquery:
      |<pre><code class="language-javascript">
      |var parent = $('.posts');
      |$.get("/posts", function(data) {
      |  $.each(data, function(index, value){
      |    var html = $.parseHTML(value.content);
      |    parent.append(html);
      |  })
      |});
      |                            </code></pre>
      |<h4>Conclusion</h4>
      |Hope this post was useful and would server as very simple example of scala world capabilities.
      |Next I am going to write about admin creation, post builder or performance testing of this blog.
      |</p>""".stripMargin

}
