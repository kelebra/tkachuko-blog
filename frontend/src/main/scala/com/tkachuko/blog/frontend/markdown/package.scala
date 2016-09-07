package com.tkachuko.blog.frontend

import scala.annotation.tailrec

package object markdown {

  object Abstractions {

    import Util._

    case class Tag(raw: String, rendered: String) {

      def isIn(value: String, offset: Int): Option[Replacement] =
        value.optionalIndexOf(raw, offset)
          .map(at => Replacement(at, raw.length))
          .orElse(
            value.optionalIndexOf(rendered, offset)
              .map(at => Replacement(at, rendered.length))
          )
    }

    abstract class Block(val open: Tag, val close: Tag, val nested: Boolean = false) {

      def startsIn(input: String, offset: Int) = open.isIn(input, offset)

      def endsIn(input: String, offset: Int) = close.isIn(input, offset)
    }

    case class H(i: Int) extends Block(
      Tag(s"${(1 to i).map(_ => "#").mkString} ", s"<h$i>"), Tag("\n", s"</h$i>\n")
    )

    case class Language(name: String) extends Block(
      Tag(s"```$name\n", s"<pre><code class = 'language-$name'>\n"),
      Tag(s"```\n", s"</code></pre>\n")
    )

    case object Italic extends Block(
      Tag("_", "<i>"), Tag("_", "</i>"), nested = true
    )

    case object Bold extends Block(
      Tag("__", "<b>"), Tag("__", "</b>"), nested = true
    )

    case object InlineCode extends Block(
      Tag("`", "<code>"), Tag("`", "</code>")
    )

    type Blocks = List[Block]

    val languages: Blocks = List("scala", "javascript", "java", "bash").map(Language.apply) ::: InlineCode :: Nil

    val headings: Blocks = 6.to(1, -1).map(H.apply).toList

    val supported: Blocks = languages ::: headings ::: Bold :: Italic :: Nil

    case class Replacement(index: Int, length: Int) {

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
        acc + (
          part match {
            case Left(rendered) => rendered.block.open.rendered + rendered.value + rendered.block.close.rendered
            case Right(value) => value
          })
      }

      def +(partition: Partition): Partitioned = copy(parts = parts :+ partition)

      def ~>(block: Block): Partitioned = ???
    }

    object Partitioned {

      def apply(input: String, by: Block) = partition(input, by)

      @tailrec
      private def partition(input: String, by: Block, offset: Int = 0,
                            acc: Partitioned = Partitioned()): Partitioned = {
        if (offset >= input.length) acc
        else {
          lazy val `default` = acc.copy(parts = acc.parts :+ Right(input.substring(offset)))

          by.startsIn(input, offset) match {
            case Some(start) =>
              by.endsIn(input, start.index + 1) match {
                case Some(end) =>
                  partition(
                    input, by, end.end,
                    acc +
                      Right(input.substring(offset, start.index)) +
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

    def md: String = ???
  }

}
