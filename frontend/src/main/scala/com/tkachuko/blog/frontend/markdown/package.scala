package com.tkachuko.blog.frontend

import scala.annotation.tailrec

package object markdown {

  case class Tag(value: String, replacement: String)

  private implicit class TaggedString(in: String) {

    def replaceAt(position: Int)(tag: Tag): String =
      in.substring(0, position) + in.substring(position).replaceFirst(tag.value, tag.replacement)

    def at(tag: Tag, offset: Int = 0): Int = in.indexOf(tag.value, offset + 1)
  }

  abstract class Block(open: Tag, close: Tag) {

    @tailrec
    final def render(text: String): String = {
      val start = text.at(open)
      val end = text.at(close, start)
      if (start > -1 && end > -1) {
        val recalculatedEnd = end - open.value.length + open.replacement.length
        render(text.replaceAt(start)(open).replaceAt(recalculatedEnd)(close))
      }
      else text
    }
  }

  case class H(i: Int) extends Block(
    Tag(s"${(1 to i).map(_ => "#").mkString} ", s"<h$i>"), Tag("\n", s"</h$i>\n")
  )

  case object Italic extends Block(
    Tag("_", "<i>"), Tag("_", "</i>")
  )

  case object Bold extends Block(
    Tag("__", "<b>"), Tag("__", "</b>")
  )

  case object InlineCode extends Block(
    Tag("`", "<code>"), Tag("`", "</code>")
  )

  case class Language(name: String) extends Block(
    Tag(s"```$name\n", s"<pre><code class = 'language-$name'>\n"),
    Tag(s"```", s"</code></pre>")
  )

  type Blocks = List[Block]

  private val languages: Blocks = List("scala", "java", "bash", "javascript").map(Language.apply) ::: InlineCode :: Nil

  private val headings: Blocks = 6.to(1, -1).map(H.apply).toList

  private val supportedTypes: Blocks = Bold :: Italic :: languages ::: headings ::: Nil

  /**
    * Public API
    */
  implicit class MarkdownString(text: String) {

    def md = supportedTypes.foldLeft(text) { case (state, block) => block.render(state) }
  }

}
