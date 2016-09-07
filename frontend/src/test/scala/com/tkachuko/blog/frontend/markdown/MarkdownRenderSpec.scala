package com.tkachuko.blog.frontend.markdown

import com.tkachuko.blog.frontend.markdown.Abstractions._
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

      Algorithm.Partitioned(html, Italic).render ==> html
    }

    "render content with italic" - {

      val markdown =
        """
          |Hello, this is _italic_ test.
          |Arithmetic operations like 2 * 2 should not change anything.
          |But _text_ should be selected as italic one.
        """.stripMargin

      Algorithm.Partitioned(markdown, Italic).render ==>
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

      Algorithm.Partitioned(markdown, Bold).render ==>
        """
          |Hello, this is <b>bold</b> text.
          |It should be replaced but <b>this</b> should not.
        """.stripMargin
    }

    //    "render bold and italic text together" - {
    //      val markdown =
    //        """
    //          |Hello, this is __bold__ text with something _italic_ in it.
    //          |It should be replaced but <b>this</b>, <i>yyy</i> should not.
    //        """.stripMargin
    //
    //      markdown.md ==>
    //        """
    //          |Hello, this is <b>bold</b> text with something <i>italic</i> in it.
    //          |It should be replaced but <b>this</b>, <i>yyy</i> should not.
    //        """.stripMargin
    //    }

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

      Algorithm.Partitioned(h1md, H(1)).render ==>
        """
          |<h1>h</h1>
        """.stripMargin

      Algorithm.Partitioned(h2md, H(2)).render ==>
        """
          |<h2>h</h2>
        """.stripMargin

      Algorithm.Partitioned(h3md, H(3)).render ==>
        """
          |<h3>h</h3>
        """.stripMargin

      Algorithm.Partitioned(h4md, H(4)).render ==>
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

      Algorithm.Partitioned(markdown, InlineCode).render ==>
        """
          |<code>some coding stuff is fun</code>
          |But another <code>coding</code> is not
        """.stripMargin
    }

    "not change previous post created in html" - {
      val post =
        """
          |<p>\n    I was looking at <a href=\"http://www.scalatest.org/user_guide/using_selenium\">scalatest selenium support</a>\n    for quite a while and actually wanted to try it. Surprisingly all tutorials were simple and everything\n    went smooth according to author's words, however every time I followed them I was in a lot of troubles\n    related to setup or interaction with resources of web page ending at stackoverflow\n    website looking for answers. With that fact in mind I wanted to create simple and working tutorial\n    which will be useful for both me in the future and anyone who would like to have a quickstart guide.\n</p>\n<p>If you want to skip explanations and observations you can find all the code <a href=\"https://github.com/kelebra/scalatest-selenium-example\">here</a>.\n    Let's start with simple requirement description:\n<ul>\n    <li>Web site we are testing is up and running</li>\n    <li>Testing should happen in <b>browser simulation</b> rather than actual one</li>\n    <li><b>Result of our test case</b> is an <b>email containing screenshot</b> with current state of web page</li>\n</ul>\nAlso let's describe test scenario:\n<ul>\n    <li>Go to host url</li>\n    <li>Enter value in text area</li>\n    <li>Click on submission button</li>\n    <li>Wait until new page is loaded</li>\n    <li>Grab information in specific page area</li>\n    <li>Send email with screenshot</li>\n</ul>\n<p>\n    Selenium provides great support for custom browser drivers meaning if you have your own browser\n    or something like that you can provide your own driver to selenium and carry out testing in your\n    browser. As per our requirements existing traits(<i>Chrome</i>, <i>Firefox</i>, <i>Safari</i>\n    and <i>InternetExplorer</i>) in scalatest do not satisfy our needs except one: <i>HtmlUnit</i>.\n    However it is quite troublesome implementation which tries to interpret all the webpage\n    resources (js, css). So I decided to use\n    <a href=\"https://github.com/MachinePublishers/jBrowserDriver\">jbrowserdriver</a> which\n    is basically a java browser implementation based on JavaFX. Also as per our requirements\n    we will use <a href=\"https://github.com/bbottema/simple-java-mail\">simple-java-mail</a>\n    to send an email. Below find actual versions and dependencies used:\n<pre><code class=\"language-scala\">\nlazy val projectSettings =\n  Seq(\n    scalaVersion := \"2.11.7\",\n    sbtVersion := \"0.13.7\",\n    libraryDependencies ++= Seq(\n      \"org.scalatest\" %% \"scalatest\" % \"2.2.6\",\n      \"org.seleniumhq.selenium\" % \"selenium-java\" % \"2.35.0\",\n      \"com.machinepublishers\" % \"jbrowserdriver\" % \"0.14.5\",\n      \"org.codemonkey.simplejavamail\" % \"simple-java-mail\" % \"3.1.1\"\n    )\n  )\n</code></pre>\n</p>\n<p>\n    Now let's implement simple <b>function to satisfy our scenario</b> up until email sending:\n<pre><code class=\"language-scala\">\nimport java.io.File\nimport java.util.{Timer, TimerTask}\nimport javax.activation.FileDataSource\n\nimport com.machinepublishers.jbrowserdriver.JBrowserDriver\nimport org.scalatest.concurrent.Eventually._\nimport org.scalatest.selenium.{Driver, WebBrowser}\nobject InformationExtractor extends App with Driver with WebBrowser {\n\n    go to args(0)\n    val homePageTitle = pageTitle\n\n    textArea(name(\"textAreaId\")).value = caseNumber\n\n    click on id(\"submitButtonId\")\n\n    eventually {\n      pageTitle != homePageTitle\n    }\n    val screenshot = captureTo(System.currentTimeMillis().toString)\n    val info = findAll(tagName(\"td\")).toList.map(_.text).grouped(6).map(_.mkString(\" \")).mkString(\"\n\")\n}\n</code></pre>\nNow let's come up with <b>email sending function</b>:\n<pre><code class=\"language-scala\">\ndef send(screenShot: File, text: String, login: String, password: String): Unit = {\n    new Mailer(\"smtp.gmail.com\", 587, login, password, TransportStrategy.SMTP_TLS)\n        .sendMail(\n            new Email.Builder()\n                .from(\"Me\", login)\n                .to(\"Me\", login)\n                .subject(\"Current status\")\n                .addAttachment(\"Current_status.png\", new FileDataSource(screenShot))\n                .text(text)\n                .build()\n            )\n}\n</code></pre>\nNow putting timer action to run this process every day (i.e for regression purposes) we get the\nfollowing working application:\n<pre><code class=\"language-scala\">\nimport java.io.File\nimport java.util.{Timer, TimerTask}\nimport javax.activation.FileDataSource\n\nimport com.machinepublishers.jbrowserdriver.JBrowserDriver\nimport org.codemonkey.simplejavamail.email.Email\nimport org.codemonkey.simplejavamail.{Mailer, TransportStrategy}\nimport org.scalatest.concurrent.Eventually._\nimport org.scalatest.selenium.{Driver, WebBrowser}\n\nobject InformationExtractor extends App with Driver with WebBrowser {\n\n  implicit val webDriver = new JBrowserDriver()\n\n  val host = args(0)\n  val caseNumber = args(1)\n\n  val login = args(2)\n  val password = args(3)\n\n  val task = new TimerTask {\n    override def run(): Unit = {\n      go to args(0)\n      val homePageTitle = pageTitle\n\n      textArea(name(\"textAreaId\")).value = caseNumber\n\n      click on id(\"submitButtonId\")\n\n      eventually {\n        pageTitle != homePageTitle\n      }\n\n      send(\n        captureScreenshot(System.currentTimeMillis().toString),\n        findAll(tagName(\"td\")).toList.map(_.text).grouped(6).map(_.mkString(\" \")).mkString(\"\"),\n        login,\n        password\n      )\n    }\n  }\n\n  val timer = new Timer()\n  timer.scheduleAtFixedRate(task, 0, 86400000)\n\n\n  def captureScreenshot(screenshotFileName: String): File = {\n    val screenshot = new File(System.getProperty(\"java.io.tmpdir\"), s\"$screenshotFileName.png\")\n    captureTo(screenshotFileName)\n    screenshot\n  }\n\n  def send(screenShot: File, text: String, login: String, password: String): Unit = {\n    new Mailer(\"smtp.gmail.com\", 587, login, password, TransportStrategy.SMTP_TLS)\n      .sendMail(\n        new Email.Builder()\n          .from(\"Me\", login)\n          .to(\"Me\", login)\n          .subject(\"Current status\")\n          .addAttachment(\"Current_status.png\", new FileDataSource(screenShot))\n          .text(text)\n          .build()\n      )\n  }\n}\n</code></pre>\nAnd that's it! You will get an emails with the screenshot of screen after scenario every day.\n</p>\n<p>\n    <b>IMPORTANT NOTE:</b> If you are going to run it (like me) on ubuntu or other linux distributive\n    please note that some of them have java installed without JavaFX packages. So in order to run\n    everything execute the following command on your linux machine: <b>sudo apt-get install openjfx</b>.\n</p>\n<p>\n    Hope this post will help you to speed up your start with scalatest and selenium. Happy coding.\n</p>
        """.stripMargin

      Algorithm.Partitioned(post, InlineCode).render ==> post
      Algorithm.Partitioned(post, Language("scala")).render ==> post
      Algorithm.Partitioned(post, Language("java")).render ==> post
      Algorithm.Partitioned(post, H(1)).render ==> post
      Algorithm.Partitioned(post, H(2)).render ==> post
    }

    "correctly partition markdown string with code" - {
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
          |<pre><code class = 'language-scala'>
          |case class Someone(name: String)
          |</code></pre>
          |<pre><code class = 'language-scala'>
          |case class Test(a: Int)
          |</code></pre>
          |Some text
        """.stripMargin

      Algorithm.Partitioned(markdown, Language("scala")).render ==> rendered
    }
  }
}
