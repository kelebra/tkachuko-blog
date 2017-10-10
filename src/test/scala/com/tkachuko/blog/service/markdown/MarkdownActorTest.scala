package com.tkachuko.blog.service.markdown

import akka.actor.ActorSystem
import com.tkachuko.blog.common.ActorSpec

class MarkdownActorTest extends ActorSpec(ActorSystem("md-actor-spec")) {

  private val md = system.actorOf(MarkdownActor())

  "Markdown actor" should {

    "not change plain HTML" in {
      val html =
        """
          |<p>
          |This is <i>sample</i> html <b>content</b>
          |With multiple lines
          |<pre><code>psvm</code></pre>
          |</p>
        """.stripMargin

      md ! html
      expectMsg(html)
    }

    "render content with italic" in {

      val markdown =
        """
          |Hello, this is _italic_ test.
          |Arithmetic operations like 2 * 2 should not change anything.
          |But _text_ should be selected as italic one.
        """.stripMargin

      md ! markdown
      expectMsg(
        """
          |Hello, this is <i>italic</i> test.
          |Arithmetic operations like 2 * 2 should not change anything.
          |But <i>text</i> should be selected as italic one.
        """.stripMargin
      )
    }

    "render content with bold text" in {

      val markdown =
        """
          |Hello, this is __bold__ text.
          |It should be replaced but <b>this</b> should not.
        """.stripMargin

      md ! markdown
      expectMsg(
        """
          |Hello, this is <b>bold</b> text.
          |It should be replaced but <b>this</b> should not.
        """.stripMargin
      )
    }

    "render bold and italic text together" in {
      val markdown =
        """
          |Hello, this is __bold__ text with something _italic_ in it.
          |It should be replaced but <b>this</b>, <i>yyy</i> should not.
        """.stripMargin

      md ! markdown
      expectMsg(
        """
          |Hello, this is <b>bold</b> text with something <i>italic</i> in it.
          |It should be replaced but <b>this</b>, <i>yyy</i> should not.
        """.stripMargin
      )
    }

    "render headings up to 4" in {
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

      md ! h1md
      md ! h2md
      md ! h3md
      md ! h4md

      expectMsg(
        """
          |<h1>h</h1>
        """.stripMargin
      )

      expectMsg(
        """
          |<h2>h</h2>
        """.stripMargin
      )

      expectMsg(
        """
          |<h3>h</h3>
        """.stripMargin
      )

      expectMsg(
        """
          |<h4>h</h4>
        """.stripMargin
      )
    }

    "render inline code block" in {
      val markdown =
        """
          |`some coding stuff is fun`
          |But another <code>coding</code> is not
        """.stripMargin

      md ! markdown
      expectMsg(
        """
          |<code>some coding stuff is fun</code>
          |But another <code>coding</code> is not
        """.stripMargin
      )
    }

    "render markdown string with code" in {
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

      md ! markdown
      expectMsg(rendered)
    }

    "not render italic element inside of code block" in {
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

      md ! markdown
      expectMsg(rendered)
    }

    "render italic-bold text" in {
      val markdown = "___bold and italic___"
      val rendered = "<b><i>bold and italic</i></b>"

      md ! markdown
      expectMsg(rendered)
    }

    "render bold text with italic in it" in {
      val markdown = "__bold text with some _italic_ text__"
      val rendered = "<b>bold text with some <i>italic</i> text</b>"

      md ! markdown
      expectMsg(rendered)
    }

    "not change html code sample" in {
      val html = """<pre><code class="language-scala"></code></pre>"""
      md ! html
      expectMsg(html)
    }

    "render list in markdown content" in {
      val markdown =
        "\n\n" +
          "* _furthermostSoFar_ - number of steps it is possible to make to our current best knowledge\n" +
          "* _board_ - array(list) of values which indicate how many steps allowed at step (index) _i_" +
          "\n\n"

      md ! markdown
      expectMsg(
        "\n<ul>\n" +
          "<li><i>furthermostSoFar</i> - number of steps it is possible to make to our current best knowledge</li>\n" +
          "<li><i>board</i> - array(list) of values which indicate how many steps allowed at step (index) <i>i</i></li>\n" +
          "</ul>\n")
    }

    "render simple list content" in {
      val markdown =
        """
          |
          |* Item 1
          |* Item 2
          |
        """.stripMargin
      md ! markdown
      expectMsg(
        """
          |<ul>
          |<li>Item 1</li>
          |<li>Item 2</li>
          |</ul>
        """.stripMargin
      )
    }

    "render list content with h2" in {
      val markdown =
        """
          |Let's define what is known:
          |
          |* Size of the ladder _N_
          |* _a<sub>i</sub>_ - number of steps that you can make at step _i_
          |
          |### Task #1: define if you can win in a given game situation:
        """.stripMargin

      md ! markdown
      expectMsg(
        """
          |Let's define what is known:
          |<ul>
          |<li>Size of the ladder <i>N</i></li>
          |<li><i>a<sub>i</sub></i> - number of steps that you can make at step <i>i</i></li>
          |</ul>
          |<h3>Task #1: define if you can win in a given game situation:</h3>
        """.stripMargin
      )
    }

    "render code with type parameters" in {
      val markdown =
        """
          |Some code with diamonds:
          |
          |```java
          |Node<T> node = ...
          |```
        """.stripMargin
      md ! markdown
      expectMsg(
        """
          |Some code with diamonds:
          |
          |<pre><code class="language-java">
          |Node&lt;T&gt; node = ...
          |</code></pre>
        """.stripMargin
      )
    }

    "render graphics block with graph inside" in {
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
      md ! markdown
      expectMsg(
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
      )
    }

    "renders inside of <a> tag in case of list" in {
      val markdown =
        """
          |Some list with html reference:
          |
          |* list item with <a href = 'this_is_href'>label</a>
          |
        """.stripMargin
      md ! markdown
      expectMsg(
        """
          |Some list with html reference:
          |<ul>
          |<li>list item with <a href = 'this_is_href'>label</a></li>
          |</ul>
        """.stripMargin
      )
    }

    "renders inside of <ul> blocks but not <a>" in {
      val markdown =
        """
          |Some list with html reference:
          |<ul>
          |<li>list _item_ with <a href = 'this_is_href'>label</a></li>
          |</ul>
        """.stripMargin
      md ! markdown
      expectMsg(
        """
          |Some list with html reference:
          |<ul>
          |<li>list <i>item</i> with <a href = 'this_is_href'>label</a></li>
          |</ul>
        """.stripMargin
      )
    }
  }
}
