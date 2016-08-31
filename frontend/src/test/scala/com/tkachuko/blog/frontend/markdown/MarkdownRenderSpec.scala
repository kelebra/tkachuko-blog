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
      val markdown =
        """
          |# h1
          |## h2
          |### h3
          |#### h4
          |##### h5
          |###### h6
          |Some not rendered text
        """.stripMargin

      markdown.md ==>
        """
          |<h1>h1</h1>
          |<h2>h2</h2>
          |<h3>h3</h3>
          |<h4>h4</h4>
          |<h5>h5</h5>
          |<h6>h6</h6>
          |Some not rendered text
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

    "render multiline scala code with text formatting sample" - {
      val markdown =
        """
          |## Playing with scala
          |```scala
          |println("Hello, World!")
          |```
          |```scala
          |case class Test(a: String)
          |```
        """.stripMargin

      markdown.md ==>
        """
          |<h2>Playing with scala</h2>
          |<pre><code class = 'language-scala'>
          |println("Hello, World!")
          |</code></pre>
          |<pre><code class = 'language-scala'>
          |case class Test(a: String)
          |</code></pre>
        """.stripMargin
    }
  }
}
