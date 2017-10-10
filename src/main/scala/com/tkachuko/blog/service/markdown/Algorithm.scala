package com.tkachuko.blog.service.markdown

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}


private[markdown] object Algorithm {

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
    private def partition(input: String, by: Block, offset: Int = 0,
                          acc: List[Partition] = List.empty): List[Partition] = {
      if (offset >= input.length) acc
      else {
        lazy val `default` = acc :+ Right(input.substring(offset))

        by.startsIn(input, offset) match {
          case Some(start) =>
            by.endsIn(input, start.isRendered, start.index + 1) match {
              case Some(end) =>
                val partitions =
                  acc :+
                    Right(input.substring(offset, start.index)) :+
                    Left(Rendered(by, input.substring(start.end, end.index)))
                partition(input, by, end.end, partitions)
              case _         => `default`
            }
          case _           => `default`
        }
      }
    }
  }

  import com.tkachuko.blog.service.markdown.Elements.supported

  import scala.language.postfixOps

  def apply(input: String)(implicit ec: ExecutionContext): Future[String] = Future {
    val seed = Partitioned(input, supported.head)
    supported.tail.foldLeft(seed) { case (acc, block) => acc ~> block } render
  }

}
