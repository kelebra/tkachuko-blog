package com.tkachuko.blog.frontend

import scala.annotation.tailrec

package object markdown {

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

    abstract class Block(val open: Tag, val close: Tag, val nested: Boolean = false) {

      def startsIn(input: String, offset: Int) = open.isIn(input, offset)

      def endsIn(input: String, rendered: Boolean, offset: Int) = close.isIn(input, offset, rendered)
    }

    case class H(i: Int) extends Block(
      Tag(s"${(1 to i).map(_ => "#").mkString} ", s"<h$i>"), Tag("\n", s"</h$i>\n")
    )

    case class Language(name: String) extends Block(
      Tag(s"```$name", s"""<pre><code class="language-$name">"""),
      Tag(s"```", s"</code></pre>")
    )

    case object Href extends Block(
      Tag("<a", "<a"), Tag("</a>", "</a>")
    )

    case object Italic extends Block(
      Tag("_", "<i>"), Tag("_", "</i>"), nested = true
    )

    case object Bold extends Block(
      Tag("__", "<b>"), Tag("__", "</b>"), nested = true
    )

    case object ItalicBold extends Block(
      Tag("___", "<b><i>"), Tag("___", "</i></b>"), nested = true
    )

    case object InlineCode extends Block(
      Tag("`", "<code>"), Tag("`", "</code>")
    )

    type Blocks = List[Block]

    val languages: Blocks = List("scala", "javascript", "java", "bash").map(Language.apply) ::: InlineCode :: Nil

    val headings: Blocks = 6.to(1, -1).map(H.apply).toList

    val supported: Blocks = languages ::: headings ::: Href :: ItalicBold :: Bold :: Italic :: Nil

    case class Replacement(index: Int, length: Int, isRendered: Boolean) {

      def end = index + length
    }

    case class Rendered(block: Block, value: String)

    type Partition = Either[Rendered, String]
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
          rendered => rendered.block.open.rendered + rendered.value + rendered.block.close.rendered,
          identity[String]
        )
      }

      def +(partition: Partition): Partitioned = copy(parts = parts :+ partition)

      def ~>(block: Block): Partitioned = {
        import Partitioned._

        Partitioned(parts.flatMap(
          _.fold(
            rendered =>
              if (rendered.block.nested)
                Left(rendered.copy(value = Partitioned(rendered.value, block).render)) :: Nil
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

    import Abstractions.supported
    import Algorithm.Partitioned

    def md: String = {
      val seed = Partitioned(text, supported.head)
      supported.tail.foldLeft(seed) { case (acc, block) => acc ~> block } render
    }
  }

}
