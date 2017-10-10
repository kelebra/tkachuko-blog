package com.tkachuko.blog.service.markdown

private[markdown] object Abstractions {

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

    def startsIn(input: String, offset: Int): Option[Replacement] =
      open.isIn(input, offset)

    def endsIn(input: String, rendered: Boolean, offset: Int): Option[Replacement] =
      close.isIn(input, offset, rendered)

    def render(value: String): String = open.rendered + value + close.rendered

    def open: Tag

    def close: Tag

    def nested: Boolean = false
  }

  case class Replacement(index: Int, length: Int, isRendered: Boolean) {

    def end: Int = index + length
  }

  case class Rendered(block: Block, value: String) {

    def render: String = block render value
  }

  type Partition = Either[Rendered, String]
}
