package com.tkachuko.blog.frontend.markdown

import utest._

object MarkdownRenderSpec extends TestSuite with Md {

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

    "render list in markdown content" - {
      val markdown =
        "\n\n" +
          "* _furthermostSoFar_ - number of steps it is possible to make to our current best knowledge\n" +
          "* _board_ - array(list) of values which indicate how many steps allowed at step (index) _i_" +
          "\n\n"

      markdown.md ==>
        "\n<ul>\n" +
          "<li><i>furthermostSoFar</i> - number of steps it is possible to make to our current best knowledge</li>\n" +
          "<li><i>board</i> - array(list) of values which indicate how many steps allowed at step (index) <i>i</i></li>\n" +
          "</ul>\n"
    }

    "render simple list content" - {
      val markdown =
        """
          |
          |* Item 1
          |* Item 2
          |
        """.stripMargin
      markdown.md ==>
        """
          |<ul>
          |<li>Item 1</li>
          |<li>Item 2</li>
          |</ul>
        """.stripMargin
    }

    "render list content with h2" - {
      val markdown =
        """
          |Let's define what is known:
          |
          |* Size of the ladder _N_
          |* _a<sub>i</sub>_ - number of steps that you can make at step _i_
          |
          |### Task #1: define if you can win in a given game situation:
        """.stripMargin

      markdown.md ==>
        """
          |Let's define what is known:
          |<ul>
          |<li>Size of the ladder <i>N</i></li>
          |<li><i>a<sub>i</sub></i> - number of steps that you can make at step <i>i</i></li>
          |</ul>
          |<h3>Task #1: define if you can win in a given game situation:</h3>
        """.stripMargin
    }

    "render code with type parameters" - {
      val markdown =
        """
          |Some code with diamonds:
          |
          |```java
          |Node<T> node = ...
          |```
        """.stripMargin
      markdown.md ==>
        """
          |Some code with diamonds:
          |
          |<pre><code class="language-java">
          |Node&lt;T&gt; node = ...
          |</code></pre>
        """.stripMargin
    }

    "render graphics block with graph inside" - {
      val markdown =
        """
          |Some code with graphics tag inside:
          |
          |```graph
          |    A-->B
          |    B-->C
          |    C-->A
          |    D-->C
          |```
        """.stripMargin
      markdown.md ==>
        """
          |Some code with graphics tag inside:
          |
          |<div class="mermaid">
          |    A-->B
          |    B-->C
          |    C-->A
          |    D-->C
          |</div>
        """.stripMargin
    }

    def checkWithTimeLogging(data: Data) = {
      val (input, expected) = data
      val bytes = input.getBytes.length
      val start = System.currentTimeMillis()
      val md = input.md
      println(s"MD rendering took: ${System.currentTimeMillis() - start} ms for $bytes bytes")
      md ==> expected
    }

    "render small markdown sample" - {
      checkWithTimeLogging(small)
    }

    "render medium markdown sample" - {
      checkWithTimeLogging(medium)
    }
  }
}
