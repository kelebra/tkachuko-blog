package com.tkachuko.blog.frontend

import scala.annotation.tailrec
import scala.language.postfixOps

package object markdown {

  private val new_line = System.lineSeparator()

  object Abstractions {

    import Util._

    case class Tag(raw: String, rendered: String) {

      def isIn(value: String, offset: Int): Option[Replacement] =
        value.optionalIndexOf(raw, offset)
          .map(at => Replacement(at, raw.length, isRendered = false))
          .orElse(
            value.optionalIndexOf(rendered, offset)
              .map(at => Replacement(at, rendered.length, isRendered = true))
          )

      def isIn(value: String, offset: Int, shouldBeRendered: Boolean): Option[Replacement] = {
        val tagToSearch = if (shouldBeRendered) rendered else raw
        value.optionalIndexOf(tagToSearch, offset).map(at => Replacement(at, tagToSearch.length, shouldBeRendered))
      }
    }

    trait Block {

      def startsIn(input: String, offset: Int) = open.isIn(input, offset)

      def endsIn(input: String, rendered: Boolean, offset: Int) = close.isIn(input, offset, rendered)

      def render(value: String) = open.rendered + value + close.rendered

      def open: Tag

      def close: Tag

      def nested: Boolean = false
    }

    case class Replacement(index: Int, length: Int, isRendered: Boolean) {

      def end = index + length
    }

    case class Rendered(block: Block, value: String) {

      def render = block render value
    }

    type Partition = Either[Rendered, String]
  }

  private object Implementation {

    import com.tkachuko.blog.frontend.markdown.Abstractions.{Block, Tag}

    case class H(i: Int) extends Block {
      def open = Tag(s"${(1 to i).map(_ => "#").mkString} ", s"<h$i>")

      def close = Tag(s"$new_line", s"</h$i>$new_line")
    }

    case class Language(name: String) extends Block {

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

    object Graphics extends Language("graph") {

      override val escape = Map.empty[String, String]

      override def open: Tag = super.open.copy(rendered = "<div class=\"mermaid\">")

      override def close: Tag = super.close.copy(rendered = "</div>")
    }

    case object HrefIgnorance extends Block {

      def open = Tag("<a", "<a")

      def close = Tag("</a>", "</a>")
    }

    case object Italic extends Block {

      def open = Tag("_", "<i>")

      def close = Tag("_", "</i>")

      override def nested = true
    }

    case object Bold extends Block {

      def open = Tag("__", "<b>")

      def close = Tag("__", "</b>")

      override def nested = true

    }

    case object ItalicBold extends Block {
      def open = Tag("___", "<b><i>")

      def close = Tag("___", "</i></b>")

      override def nested = true
    }


    case object InlineCode extends Block {

      def open = Tag("`", "<code>")

      def close = Tag("`", "</code>")
    }

    case object OrderedList extends Block {

      def open = Tag(s"$new_line$new_line", s"$new_line<ul>$new_line")

      def close = Tag(s"$new_line$new_line", s"$new_line</ul>$new_line")

      override def nested = true

      val liEncoded = "* "
      val liDecoded = "<li>"
      val liSuffix = "</li>"

      override def render(value: String): String = {
        val lines = value.split(s"$new_line")
        super.render(
          lines.map {
            case li if li.startsWith(liEncoded) => liDecoded + li.substring(liEncoded.length) + liSuffix
            case other => other
          }.mkString(s"$new_line")
        )
      }
    }

    type Blocks = List[Block]

    val languages: Blocks = List("scala", "javascript", "java", "bash").map(Language.apply) :::
      Graphics :: InlineCode :: Nil

    val headings: Blocks = 6.to(1, -1).map(H.apply).toList

    val supported: Blocks = languages ::: headings ::: OrderedList :: HrefIgnorance ::
      ItalicBold :: Bold :: Italic :: Nil
  }

  private object Util {

    implicit class StringOps(input: String) {

      def optionalIndexOf(other: String, from: Int): Option[Int] =
        Option(input.indexOf(other, from)).filterNot(_ == -1)
    }

  }

  object Algorithm {

    import Abstractions._

    case class Partitioned(parts: List[Partition] = List.empty) {

      def render: String = parts.foldLeft("") { case (acc, part: Partition) =>
        acc + part.fold(
          rendered => rendered.block.render(rendered.value),
          identity[String]
        )
      }

      def +(partition: Partition): Partitioned = copy(parts = parts :+ partition)

      def ~>(block: Block): Partitioned = {
        import Partitioned._

        Partitioned(parts.flatMap(
          _.fold(
            rendered =>
              if (rendered.block.nested) partition(rendered.render, block)
              else Left(rendered) :: Nil,
            value => partition(value, block)
          )
        ))
      }
    }

    object Partitioned {

      def apply(input: String, by: Block): Partitioned = Partitioned(partition(input, by))

      @tailrec
      def partition(input: String, by: Block, offset: Int = 0,
                    acc: List[Partition] = List.empty): List[Partition] = {
        if (offset >= input.length) acc
        else {
          lazy val `default` = acc :+ Right(input.substring(offset))

          by.startsIn(input, offset) match {
            case Some(start) =>
              by.endsIn(input, start.isRendered, start.index + 1) match {
                case Some(end) =>
                  partition(
                    input, by, end.end,
                    acc :+
                      Right(input.substring(offset, start.index)) :+
                      Left(Rendered(by, input.substring(start.end, end.index)))
                  )
                case _ => `default`
              }
            case _ => `default`
          }
        }
      }
    }

  }

  /**
    * Public API
    */
  implicit class MarkdownString(text: String) {

    import Algorithm.Partitioned
    import Implementation.supported

    def md: String = {
      val seed = Partitioned(text, supported.head)
      supported.tail.foldLeft(seed) { case (acc, block) => acc ~> block } render
    }
  }

}
