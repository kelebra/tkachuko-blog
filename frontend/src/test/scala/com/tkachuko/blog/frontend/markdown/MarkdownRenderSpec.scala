package com.tkachuko.blog.frontend.markdown

import utest._

object MarkdownRenderSpec extends TestSuite {

  val tests = TestSuite {


    "not change plain HTML" - {
      val html =
        """
          |<p>
          |This is <i>sample</i> html <b>content</b>
          |With multiple lines
          |<pre><code>psvm</code></pre>
          |</p>
        """.stripMargin

      html.md ==> html
    }

    "render content with italic" - {

      val markdown =
        """
          |Hello, this is _italic_ test.
          |Arithmetic operations like 2 * 2 should not change anything.
          |But _text_ should be selected as italic one.
        """.stripMargin

      markdown.md ==>
        """
          |Hello, this is <i>italic</i> test.
          |Arithmetic operations like 2 * 2 should not change anything.
          |But <i>text</i> should be selected as italic one.
        """.stripMargin
    }

    "render content with bold text" - {

      val markdown =
        """
          |Hello, this is __bold__ text.
          |It should be replaced but <b>this</b> should not.
        """.stripMargin

      markdown.md ==>
        """
          |Hello, this is <b>bold</b> text.
          |It should be replaced but <b>this</b> should not.
        """.stripMargin
    }

    "render bold and italic text together" - {
      val markdown =
        """
          |Hello, this is __bold__ text with something _italic_ in it.
          |It should be replaced but <b>this</b>, <i>yyy</i> should not.
        """.stripMargin

      markdown.md ==>
        """
          |Hello, this is <b>bold</b> text with something <i>italic</i> in it.
          |It should be replaced but <b>this</b>, <i>yyy</i> should not.
        """.stripMargin
    }

    "render headings up to 6" - {
      val h1md =
        """
          |# h
        """.stripMargin

      val h2md =
        """
          |## h
        """.stripMargin

      val h3md =
        """
          |### h
        """.stripMargin

      val h4md =
        """
          |#### h
        """.stripMargin

      h1md.md ==>
        """
          |<h1>h</h1>
        """.stripMargin

      h2md.md ==>
        """
          |<h2>h</h2>
        """.stripMargin

      h3md.md ==>
        """
          |<h3>h</h3>
        """.stripMargin

      h4md.md ==>
        """
          |<h4>h</h4>
        """.stripMargin
    }

    "render inline code block" - {
      val markdown =
        """
          |`some coding stuff is fun`
          |But another <code>coding</code> is not
        """.stripMargin

      markdown.md ==>
        """
          |<code>some coding stuff is fun</code>
          |But another <code>coding</code> is not
        """.stripMargin
    }

    "render markdown string with code" - {
      val markdown =
        """
          |Some text
          |```scala
          |case class Someone(name: String)
          |```
          |```scala
          |case class Test(a: Int)
          |```
          |Some text
        """.stripMargin

      val rendered =
        """
          |Some text
          |<pre><code class="language-scala">
          |case class Someone(name: String)
          |</code></pre>
          |<pre><code class="language-scala">
          |case class Test(a: Int)
          |</code></pre>
          |Some text
        """.stripMargin

      markdown.md ==> rendered
    }

    "not render italic element inside of code block" - {
      val markdown =
        """
          |Some text
          |```scala
          |case class Someone(name: String)
          |_do not render_
          |```
          |```scala
          |case class Test(a: Int)
          |_do not render_
          |```
          |_some italic text_
          |Some text
        """.stripMargin

      val rendered =
        """
          |Some text
          |<pre><code class="language-scala">
          |case class Someone(name: String)
          |_do not render_
          |</code></pre>
          |<pre><code class="language-scala">
          |case class Test(a: Int)
          |_do not render_
          |</code></pre>
          |<i>some italic text</i>
          |Some text
        """.stripMargin

      markdown.md ==> rendered
    }

    "render italic-bold text" - {
      val markdown = "___bold and italic___"
      val rendered = "<b><i>bold and italic</i></b>"

      markdown.md ==> rendered
    }

    "render bold text with italic in it" - {
      val markdown = "__bold text with some _italic_ text__"
      val rendered = "<b>bold text with some <i>italic</i> text</b>"

      markdown.md ==> rendered
    }

    "not change html code sample" - {
      val html = """<pre><code class="language-scala"></code></pre>"""
      html.md ==> html
    }

    "not change previous post created in html" - {
      val post =
        "<p>\n    I was looking at <a href=\"http://www.scalatest.org/user_guide/using_selenium\">scalatest selenium support</a>\n    for quite a while and actually wanted to try it. Surprisingly all tutorials were simple and everything\n    went smooth according to author's words, however every time I followed them I was in a lot of troubles\n    related to setup or interaction with resources of web page ending at stackoverflow\n    website looking for answers. With that fact in mind I wanted to create simple and working tutorial\n    which will be useful for both me in the future and anyone who would like to have a quickstart guide.\n</p>\n<p>If you want to skip explanations and observations you can find all the code <a href=\"https://github.com/kelebra/scalatest-selenium-example\">here</a>.\n    Let's start with simple requirement description:\n<ul>\n    <li>Web site we are testing is up and running</li>\n    <li>Testing should happen in <b>browser simulation</b> rather than actual one</li>\n    <li><b>Result of our test case</b> is an <b>email containing screenshot</b> with current state of web page</li>\n</ul>\nAlso let's describe test scenario:\n<ul>\n    <li>Go to host url</li>\n    <li>Enter value in text area</li>\n    <li>Click on submission button</li>\n    <li>Wait until new page is loaded</li>\n    <li>Grab information in specific page area</li>\n    <li>Send email with screenshot</li>\n</ul>\n<p>\n    Selenium provides great support for custom browser drivers meaning if you have your own browser\n    or something like that you can provide your own driver to selenium and carry out testing in your\n    browser. As per our requirements existing traits(<i>Chrome</i>, <i>Firefox</i>, <i>Safari</i>\n    and <i>InternetExplorer</i>) in scalatest do not satisfy our needs except one: <i>HtmlUnit</i>.\n    However it is quite troublesome implementation which tries to interpret all the webpage\n    resources (js, css). So I decided to use\n    <a href=\"https://github.com/MachinePublishers/jBrowserDriver\">jbrowserdriver</a> which\n    is basically a java browser implementation based on JavaFX. Also as per our requirements\n    we will use <a href=\"https://github.com/bbottema/simple-java-mail\">simple-java-mail</a>\n    to send an email. Below find actual versions and dependencies used:\n<pre><code class=\"language-scala\">\nlazy val projectSettings =\n  Seq(\n    scalaVersion := \"2.11.7\",\n    sbtVersion := \"0.13.7\",\n    libraryDependencies ++= Seq(\n      \"org.scalatest\" %% \"scalatest\" % \"2.2.6\",\n      \"org.seleniumhq.selenium\" % \"selenium-java\" % \"2.35.0\",\n      \"com.machinepublishers\" % \"jbrowserdriver\" % \"0.14.5\",\n      \"org.codemonkey.simplejavamail\" % \"simple-java-mail\" % \"3.1.1\"\n    )\n  )\n</code></pre>\n</p>\n<p>\n    Now let's implement simple <b>function to satisfy our scenario</b> up until email sending:\n<pre><code class=\"language-scala\">\nimport java.io.File\nimport java.util.{Timer, TimerTask}\nimport javax.activation.FileDataSource\n\nimport com.machinepublishers.jbrowserdriver.JBrowserDriver\nimport org.scalatest.concurrent.Eventually._\nimport org.scalatest.selenium.{Driver, WebBrowser}\nobject InformationExtractor extends App with Driver with WebBrowser {\n\n    go to args(0)\n    val homePageTitle = pageTitle\n\n    textArea(name(\"textAreaId\")).value = caseNumber\n\n    click on id(\"submitButtonId\")\n\n    eventually {\n      pageTitle != homePageTitle\n    }\n    val screenshot = captureTo(System.currentTimeMillis().toString)\n    val info = findAll(tagName(\"td\")).toList.map(_.text).grouped(6).map(_.mkString(\" \")).mkString(\"\n\")\n}\n</code></pre>\nNow let's come up with <b>email sending function</b>:\n<pre><code class=\"language-scala\">\ndef send(screenShot: File, text: String, login: String, password: String): Unit = {\n    new Mailer(\"smtp.gmail.com\", 587, login, password, TransportStrategy.SMTP_TLS)\n        .sendMail(\n            new Email.Builder()\n                .from(\"Me\", login)\n                .to(\"Me\", login)\n                .subject(\"Current status\")\n                .addAttachment(\"Current_status.png\", new FileDataSource(screenShot))\n                .text(text)\n                .build()\n            )\n}\n</code></pre>\nNow putting timer action to run this process every day (i.e for regression purposes) we get the\nfollowing working application:\n<pre><code class=\"language-scala\">\nimport java.io.File\nimport java.util.{Timer, TimerTask}\nimport javax.activation.FileDataSource\n\nimport com.machinepublishers.jbrowserdriver.JBrowserDriver\nimport org.codemonkey.simplejavamail.email.Email\nimport org.codemonkey.simplejavamail.{Mailer, TransportStrategy}\nimport org.scalatest.concurrent.Eventually._\nimport org.scalatest.selenium.{Driver, WebBrowser}\n\nobject InformationExtractor extends App with Driver with WebBrowser {\n\n  implicit val webDriver = new JBrowserDriver()\n\n  val host = args(0)\n  val caseNumber = args(1)\n\n  val login = args(2)\n  val password = args(3)\n\n  val task = new TimerTask {\n    override def run(): Unit = {\n      go to args(0)\n      val homePageTitle = pageTitle\n\n      textArea(name(\"textAreaId\")).value = caseNumber\n\n      click on id(\"submitButtonId\")\n\n      eventually {\n        pageTitle != homePageTitle\n      }\n\n      send(\n        captureScreenshot(System.currentTimeMillis().toString),\n        findAll(tagName(\"td\")).toList.map(_.text).grouped(6).map(_.mkString(\" \")).mkString(\"\"),\n        login,\n        password\n      )\n    }\n  }\n\n  val timer = new Timer()\n  timer.scheduleAtFixedRate(task, 0, 86400000)\n\n\n  def captureScreenshot(screenshotFileName: String): File = {\n    val screenshot = new File(System.getProperty(\"java.io.tmpdir\"), s\"$screenshotFileName.png\")\n    captureTo(screenshotFileName)\n    screenshot\n  }\n\n  def send(screenShot: File, text: String, login: String, password: String): Unit = {\n    new Mailer(\"smtp.gmail.com\", 587, login, password, TransportStrategy.SMTP_TLS)\n      .sendMail(\n        new Email.Builder()\n          .from(\"Me\", login)\n          .to(\"Me\", login)\n          .subject(\"Current status\")\n          .addAttachment(\"Current_status.png\", new FileDataSource(screenShot))\n          .text(text)\n          .build()\n      )\n  }\n}\n</code></pre>\nAnd that's it! You will get an emails with the screenshot of screen after scenario every day.\n</p>\n<p>\n    <b>IMPORTANT NOTE:</b> If you are going to run it (like me) on ubuntu or other linux distributive\n    please note that some of them have java installed without JavaFX packages. So in order to run\n    everything execute the following command on your linux machine: <b>sudo apt-get install openjfx</b>.\n</p>\n<p>\n    Hope this post will help you to speed up your start with scalatest and selenium. Happy coding.\n</p>"
      post.md ==> post
    }

    "not change large previous post in html" - {
      val post = "<p>\n    For those of you who is as impatient as I am and would like to dive into source code on your\n    own please follow <a href=\"https://github.com/kelebra/tkachuko-blog\">this link</a> which\n    will take you directly to my github repository. Please use master for latest version.\n    <br/>\n    For the ones who would like to get step by step guidance, you are welcome. Let's shape what\n    we would like to achieve: a simple blog which will load posts from database. For simplicity\n    of this very demo we are going to store posts as plain html. On the startup we are going to\n    load them in chronological order and display on a single page.\n    <br/>\n    Let's define our technological stack:\n<ul>\n    <li>Scala (as main programming language)</li>\n    <li>Sbt (to build our project)</li>\n    <li>Akka-http (for rest API and http interaction)</li>\n    <li>skinny-framework (for database interaction)</li>\n    <li>postgres and h2 (databases for production and tests)</li>\n</ul>\n<h4>Project layout and build</h4>\nAs we are going to use sbt we will have to follow its (or better to say maven)\n<a href=\"http://www.scala-sbt.org/0.13/docs/Directories.html\">project structure</a>.\nThe difference between sbt and maven is possibility to programmatically describe subprojects,\ndependencies and everything else you might need for your project build. Let's start defining our\nmodules which are going to split our codebase into some logical pieces. Below you can find\n<i>build.sbt</i> file defined in the root folder of the project:\n<pre><code class=\"language-scala\">\nimport Configuration._\nlazy val root = (project in file(\".\")).settings(rootSettings: _*).aggregate(models, dbAccess, backend)\nlazy val dbAccess = (project in file(\"db-access\")).settings(dbAccessSettings: _*).dependsOn(models)\nlazy val models = (project in file(\"models\")).settings(modelsSettings: _*)\nlazy val backend = (project in file(\"backend\")).settings(backendSettings: _*).dependsOn(dbAccess)\n</code></pre>\nAs you can see here we decided to define our project settings programmatically in the object\n<i>Configuration.scala</i>. This file is located in <i>project</i> folder:\n<pre><code class=\"language-scala\">\nimport Dependencies._\nimport sbt.Keys._\nimport sbt._\n\nobject Configuration {\n\n  lazy val commonSettings =\n    Seq(\n       organization := \"com.tkachuko.blog\",\n       version := \"1.0\",\n       scalaVersion := Versions.scala,\n       scalaBinaryVersion := Versions.scalaBinary,\n       sbtVersion := Versions.sbt,\n       libraryDependencies ++= Seq(scalaTest),\n       parallelExecution in Test := false,\n       dependencyOverrides += \"org.scala-lang\" % \"scala-compiler\" % scalaVersion.value,\n    )\n\n  lazy val modelsSettings = commonSettings\n\n  lazy val dbAccessSettings = commonSettings :+ {\n    libraryDependencies ++= Seq(typesafeConfig, h2, orm)\n  }\n\n  lazy val backendSettings = commonSettings :+ {\n    libraryDependencies ++= Seq(http, testkit, json, h2, postgres)\n  }\n\n  lazy val rootSettings = commonSettings\n\n  object Versions {\n    val scala = \"2.11.7\"\n    val scalaBinary = \"2.11\"\n    val sbt = \"0.13.7\"\n  }\n\n}\n\nobject Dependencies {\n\n  val scalaTest: ModuleID = \"org.scalatest\" %% \"scalatest\" % \"2.2.4\" % \"test\"\n  val orm: ModuleID = \"org.skinny-framework\" %% \"skinny-orm\" % \"2.0.7\"\n  val postgres = \"org.postgresql\" % \"postgresql\" % \"9.4.1208\"\n  val h2: ModuleID = \"com.h2database\" % \"h2\" % \"1.3.168\" % \"test\"\n  val typesafeConfig: ModuleID = \"com.typesafe\" % \"config\" % \"1.3.0\"\n  val http: ModuleID = \"com.typesafe.akka\" %% \"akka-http-experimental\" % \"2.4.2\"\n  val json: ModuleID = \"com.typesafe.akka\" %% \"akka-http-spray-json-experimental\" % \"2.4.2\"\n  val testkit: ModuleID = \"com.typesafe.akka\" %% \"akka-http-testkit-experimental\" % \"2.4.2-RC3\"\n}\n                            </code></pre>\nWe try to separate responsibilities in our build defining every part of build configuration in\na separate object. That is the main reason objects <i>Configuration</i>, <i>Dependencies</i> and\n<i>Versions</i> were introduced. As a result our multi-project definition (<i>build.sbt</i>) is\nnot aware of particular settings for any specific sub-project and is clean and readable.\n<h4>Domain models</h4>\nWe want to keep our domain model as simple as possible so we are going to represent our blog\npost in the following way:\n<pre><code class=\"language-scala\">\npackage object models {\n\n  case class Post(id: Long = System.currentTimeMillis(), title: String, content: String)\n}\n                            </code></pre>\nMillis is more than enough for our domestic blog (unless you are going to post multiple posts in\none millisecond).\n<h4>Database interaction and ORM</h4>\nNow let's define our <i>db-access</i> module and let's define our requirements with this very\nsimple test <i>DatabaseSpec.scala</i>:\n<pre><code class=\"language-scala\">\nimport com.tkachuko.blog.models.Post\nimport org.h2.tools.Server\nimport org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}\n\nclass DatabaseSpec extends WordSpec with Matchers with BeforeAndAfterAll {\n\n  val id = System.currentTimeMillis()\n\n  \"Database\" should {\n\n    \"retrieve persisted record by id\" in {\n      Database.Posts.findById(id) should be('defined)\n    }\n  }\n\n  override protected def beforeAll(): Unit = {\n    Server.createTcpServer(\"-tcpAllowOthers\").start()\n    Database.initialize()\n    Database.save(Post(id = id, title = \"title\", content = \"content\"))\n  }\n}\n                            </code></pre>\nThis test starts in-memory h2 database and first of all tries to save an entity and after that\ntries to verify that it exists. Now, let's implement <i>Database</i> class using <i>skinny-orm</i>:\n<pre><code class=\"language-scala\">\nimport com.tkachuko.blog.models.Post\nimport com.typesafe.config.ConfigFactory\nimport scalikejdbc.{AutoSession, WrappedResultSet, _}\nimport skinny.DBSettings\nimport skinny.orm.{Alias, SkinnyCRUDMapper}\n\npackage object db {\n\n  val config = ConfigFactory.load(\"application.conf\")\n  val init = config.getBoolean(\"development.init\")\n\n  object Database {\n\n    implicit val session = AutoSession\n\n    def initialize(): Unit = {\n      DBSettings.initialize()\n      if (init) {\n        sql\"drop table if exists POSTS;\".execute().apply()\n        sql\"create table POSTS (id serial, title varchar(50), content varchar(100000));\".execute().apply()\n      }\n    }\n\n    object Posts extends SkinnyCRUDMapper[Post] {\n\n      override def defaultAlias: Alias[Post] = createAlias(\"p\")\n\n      override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Post]): Post =\n        Post(rs.long(n.id), rs.string(n.title), rs.string(n.content))\n\n      override def tableName: String = \"POSTS\"\n    }\n\n    def save(post: Post) =\n      sql\"insert into POSTS (id, title, content) values(${post.id}, ${post.title}, ${post.content});\".execute().apply()\n  }\n\n}\n                            </code></pre>\nAs you can see we are using plain SQL here to create required table and query it. Object <i>Posts</i>\nrepresents table mapping defining transformation (method <i>extract</i>) from raw table row\nalong with table tame itself (method <i>tableName</i>). Pretty much simple, right? Also,\nas you can see <i>skinny</i> requires <i>application.conf</i> file to be present in the classpath.\nBelow you can find configuration required for postgres and h2 respectively:\n<pre><code class=\"language-javascript\">\ndevelopment {\n  db {\n    default {\n      driver = \"org.postgresql.Driver\"\n      url = \"jdbc:postgresql://!host!:!port!/!db-name!\"\n      user = \"###\"\n      password = \"###\"\n      poolInitialSize = 2\n      poolMaxSize = 10\n    }\n  }\n  init = false\n}\n                            </code></pre>\n<pre><code class=\"language-javascript\">\ndevelopment {\n  db {\n    default {\n      driver = \"org.h2.Driver\"\n      url = \"jdbc:h2:mem:example\"\n      user = \"sa\"\n      password = \"sa\"\n      poolInitialSize = 2\n      poolMaxSize = 10\n    }\n  }\n  init = true\n}\n                            </code></pre>\n<h4>HTTP Server</h4>\nNow let's define our web server and routes (module <i>backend</i>). And again let's start from\nsimple spec (<i>RoutesSpec.scala</i>):\n<pre><code class=\"language-scala\">\nimport akka.http.scaladsl.model.StatusCodes\nimport akka.http.scaladsl.testkit.ScalatestRouteTest\nimport com.tkachuko.blog.backend.WebServer.routes\nimport com.tkachuko.blog.backend.static.StaticDataResolver._\nimport com.tkachuko.blog.db.Database\nimport com.tkachuko.blog.models.{Post => BlogPost}\nimport org.h2.tools.Server\nimport org.scalatest.{Matchers, WordSpec}\n\nclass RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {\n\n  \"Web server\" should {\n\n    \"return homepage for GET request to the root path\" in {\n      Get() ~> routes ~> check {\n        status === StatusCodes.Success\n        responseAs[String] should not be empty\n      }\n    }\n\n    \"return static resource for GET request to the /pages/css/index.css\" in {\n      Get(s\"/$resourcePrefix/css/index.css\") ~> routes ~> check {\n        status === StatusCodes.Success\n        responseAs[String] should not be empty\n      }\n    }\n\n    \"return all posts as json for GET to the /posts\" in {\n      Get(s\"/$posts\") ~> routes ~> check {\n        status === StatusCodes.Success\n        responseAs[String] should not be empty\n      }\n    }\n  }\n\n  override protected def beforeAll(): Unit = {\n    Server.createTcpServer(\"-tcpAllowOthers\").start()\n    Database.initialize()\n    Database.save(BlogPost(1, \"title\", \"content\"))\n    Database.save(BlogPost(2, \"title other\", \"content\"))\n  }\n}\n                            </code></pre>\nSo requirements to our web server are pretty much simple:\n<ul>\n    <li>Return homepage</li>\n    <li>Return static resources</li>\n    <li>Return all blog posts</li>\n</ul>\nwhich are reflected in these test cases. Before jumping into routes definition let's provide\njson serialization for our domain model:\n<pre><code class=\"language-scala\">\nimport com.tkachuko.blog.models.Post\nimport spray.json._\n\nobject PostJsonSupport extends DefaultJsonProtocol {\n\n  implicit val jsonFormat = jsonFormat3(Post)\n}\n                            </code></pre>\nNow having everything in place, let's define routes for our web server:\n<pre><code class=\"language-scala\">\nimport akka.actor.ActorSystem\nimport akka.http.scaladsl.Http\nimport akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._\nimport akka.http.scaladsl.server.Directives._\nimport akka.stream.ActorMaterializer\nimport com.tkachuko.blog.backend.json.PostJsonSupport._\nimport com.tkachuko.blog.backend.static.StaticDataResolver._\nimport com.tkachuko.blog.db.Database\nimport spray.json._\n\nobject WebServer {\n\n  implicit val system = ActorSystem(\"tkachuko-system\")\n  implicit val mat = ActorMaterializer()\n  implicit val ec = system.dispatcher\n\n  val routes =\n    get {\n      pathSingleSlash {\n        homePage\n      } ~\n      path(resourcePrefix / Rest) { resource =>\n        resource.asWebResource\n      } ~\n      path(blog) {\n        blogPage\n      } ~\n      path(posts) {\n        complete(Database.Posts.findAllModels().toJson)\n      }\n    }\n\n    def main(args: Array[String]): Unit = {\n      val binding = Http().bindAndHandle(routes, args(0), args(1).toInt)\n\n      Database.initialize()\n\n      binding.onFailure {\n        case e: Exception =>\n          println(e)\n          system.terminate()\n    }\n  }\n}                           </code></pre>\nHere we are using simplest capabilities of akka route dsl defining GET method routes for\nfour different URLs. Also, as you probably guessed, we are going to run <i>WebServer.scala</i>\npassing host and port parameters. To complete the picture, find implicits used below:\n<pre><code class=\"language-scala\">\nimport akka.http.scaladsl.server.Directives._\n\nobject StaticDataResolver {\n\n  val resourcePrefix = \"pages\"\n\n  val blog = \"blog\"\n\n  val posts = \"posts\"\n\n  val homePage = \"index.html\".asWebResource\n\n  val blogPage = \"blog.html\".asWebResource\n\n  implicit class WebResource(val path: String) extends AnyVal {\n\n    def asWebResource = getFromResource(s\"$resourcePrefix/$path\")\n  }\n\n}\n                            </code></pre>\n<h4>UI</h4>\nWe are going to send a request to server on page load to load all posts using jquery:\n<pre><code class=\"language-javascript\">\nvar parent = $('.posts');\n$.get(\"/posts\", function(data) {\n  $.each(data, function(index, value){\n    var html = $.parseHTML(value.content);\n    parent.append(html);\n  })\n});\n                            </code></pre>\n<h4>Conclusion</h4>\nHope this post was useful and would server as very simple example of scala world capabilities.\nNext I am going to write about admin creation, post builder or performance testing of this blog.\n</p>"
      post.md ==> post
    }
  }
}
