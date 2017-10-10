package com.tkachuko.blog.service.markdown

import com.tkachuko.blog.service.markdown.Abstractions.{Block, Tag}

private[markdown] object Elements {

  private case class H(i: Int) extends Block {

    def open = Tag(s"${(1 to i).map(_ => "#").mkString} ", s"<h$i>")

    def close = Tag("\n", s"</h$i>\n")
  }

  private case class Language(name: String) extends Block {

    def open = Tag(s"```$name", s"""<pre><code class="language-$name">""")

    def close = Tag(s"```", s"</code></pre>")

    val escape = Map(
      "<" -> "&lt;",
      ">" -> "&gt;"
    )

    override def render(value: String): String = super.render(
      escape.foldLeft(value) { case (state, (utf, html)) => state.replaceAll(utf, html) }
    )
  }

  private object Graphics extends Language("graph") {

    override val escape = Map.empty[String, String]

    override def open: Tag = super.open.copy(rendered = "<div class=\"mermaid\">")

    override def close: Tag = super.close.copy(rendered = "</div>")
  }

  private case object HrefIgnorance extends Block {

    def open = Tag("<a", "<a")

    def close = Tag("</a>", "</a>")
  }

  private case object Italic extends Block {

    def open = Tag("_", "<i>")

    def close = Tag("_", "</i>")

    override def nested = true
  }

  private case object Bold extends Block {

    def open = Tag("__", "<b>")

    def close = Tag("__", "</b>")

    override def nested = true

  }

  private case object ItalicBold extends Block {
    def open = Tag("___", "<b><i>")

    def close = Tag("___", "</i></b>")

    override def nested = true
  }


  private case object InlineCode extends Block {

    def open = Tag("`", "<code>")

    def close = Tag("`", "</code>")
  }

  private case object OrderedList extends Block {

    def open = Tag("\n\n", "\n<ul>\n")

    def close = Tag("\n\n", "\n</ul>\n")

    override def nested = true

    val liEncoded = "* "
    val liDecoded = "<li>"
    val liSuffix = "</li>"

    override def render(value: String): String = {
      val lines = value.split("\n")
      super.render(
        lines.map {
          case li if li.startsWith(liEncoded) => liDecoded + li.substring(liEncoded.length) + liSuffix
          case other                          => other
        }.mkString("\n")
      )
    }
  }

  private type Blocks = List[Block]

  val languages: Blocks = List("scala", "javascript", "java", "bash").map(Language.apply) :::
    Graphics :: InlineCode :: Nil

  val headings: Blocks = 6.to(1, -1).map(H.apply).toList

  val supported: Blocks = languages ::: headings ::: OrderedList :: HrefIgnorance ::
    ItalicBold :: Bold :: Italic :: Nil
}
