package com.tkachuko.blog.frontend

import scala.annotation.tailrec

package object markdown {

  object Abstractions {

    import Util._

    case class Tag(raw: String, rendered: String) {

      def isIn(value: String, offset: Int): Option[Replacement] =
        value.optionalIndexOf(raw, offset)
          .map(at => Replacement(at, raw, rendered))
          .orElse(
            value.optionalIndexOf(rendered, offset)
              .map(at => Replacement(at, rendered, rendered))
          )

      lazy val chunk = math.max(raw.length, rendered.length)
    }

    abstract class Block(val open: Tag, val close: Tag, val nested: Boolean = false) {

      def startsIn(input: String, offset: Int) = open.isIn(input, offset)

      def endsIn(input: String, offset: Int) = close.isIn(input, offset)

      lazy val chunk = math.max(open.chunk, close.chunk)
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

    val languages: Blocks = List("scala", "java", "bash", "javascript").map(Language.apply) ::: InlineCode :: Nil

    val headings: Blocks = 6.to(1, -1).map(H.apply).toList

    val supported: Blocks = Bold :: Italic :: languages ::: headings ::: Nil

    val nestedBlocks: Map[Boolean, Blocks] = supported.groupBy(_.nested)

    case class Replacement(at: Int, previous: String, replacement: String) {
      def start = at

      def end = at + previous.length
    }

  }

  private object Util {

    implicit class StringOps(input: String) {

      def optionalIndexOf(other: String, from: Int): Option[Int] =
        Option(input.indexOf(other, from)).filterNot(_ == -1)
    }

  }

  object Algorithm {

    import Abstractions._

    type Partition = Either[Replacement, String]

    case class Partitioned(parts: List[Partition] = List.empty) {

      def render: String = parts.foldLeft("") { case (acc, part: Partition) =>
        acc + (
          part match {
            case Left(value) => value.replacement
            case Right(value) => value
          })
      }
    }

    @tailrec
    def partition(input: String, by: Block, offset: Int = 0,
                  partitions: Partitioned = Partitioned()): Partitioned = {
      if (offset >= input.length) partitions
      else {
        lazy val `default` = partitions.copy(parts = partitions.parts :+ Right(input.substring(offset)))

        by.startsIn(input, offset) match {
          case Some(startReplacement) =>
            by.endsIn(input, startReplacement.start + 1) match {
              case Some(endReplacement) =>
                partition(
                  input, by, endReplacement.end,
                  partitions.copy(
                    parts = partitions.parts :+
                      Right(input.substring(offset, startReplacement.start)) :+
                      Left(startReplacement) :+
                      Right(input.substring(startReplacement.end, endReplacement.start)) :+
                      Left(endReplacement)
                  )
                )
              case _ => `default`
            }
          case _ => `default`
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
