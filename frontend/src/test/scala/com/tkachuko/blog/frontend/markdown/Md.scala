package com.tkachuko.blog.frontend.markdown

trait Md {

  type Data = (String, String)

  private val markdown: Data = (
    """
      |# This is small demo of all supported features in this Markdown parser
      |
      |## Languages:
      |### Scala:
      |```scala
      |object Test {
      |    def main(args: Array[String]): Unit = println("Hello world")
      |}
      |```
      |### Javascript:
      |```javascript
      |alert("Hello world");
      |```
      |### Java:
      |```java
      |class Test {
      |    public static void main(String[] args){
      |        println("Hello world")
      |    }
      |}
      |```
      |### Bash:
      |```bash
      |ls -la
      |```
      |## Ignorance of the a element:
      |<a href = "_ignored_"></a>
      |## Italic, bold, italic-bold:
      |__hello__ - this is bold, ___hello___ - this is italic bold, _hello_ - this is italic
      |## This is lists:
      |
      |
      |* Item 1
      |* Item 2
      |
    """.stripMargin,
    """
      |<h1>This is small demo of all supported features in this Markdown parser</h1>
      |
      |<h2>Languages:</h2>
      |<h3>Scala:</h3>
      |<pre><code class="language-scala">
      |object Test {
      |    def main(args: Array[String]): Unit = println("Hello world")
      |}
      |</code></pre>
      |<h3>Javascript:</h3>
      |<pre><code class="language-javascript">
      |alert("Hello world");
      |</code></pre>
      |<h3>Java:</h3>
      |<pre><code class="language-java">
      |class Test {
      |    public static void main(String[] args){
      |        println("Hello world")
      |    }
      |}
      |</code></pre>
      |<h3>Bash:</h3>
      |<pre><code class="language-bash">
      |ls -la
      |</code></pre>
      |<h2>Ignorance of the a element:</h2>
      |<a href = "_ignored_"></a>
      |<h2>Italic, bold, italic-bold:</h2>
      |<b>hello</b> - this is bold, <b><i>hello</i></b> - this is italic bold, <i>hello</i> - this is italic
      |<h2>This is lists:</h2>
      |
      |<ul>
      |<li>Item 1</li>
      |<li>Item 2</li>
      |</ul>
    """.stripMargin
    )

  val small: Data = markdown

  val medium: Data = *(small)(20)

  val large = *(small)(50)

  private def *(data: Data)(value: Int): Data = {
    def populate(s: String): String = (0 to value)./:(s) { case (acc, _) => acc + s }
    val (raw, rendered) = data
    (populate(raw), populate(rendered))
  }
}
