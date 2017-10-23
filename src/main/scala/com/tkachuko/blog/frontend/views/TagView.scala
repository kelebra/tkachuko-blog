package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.router.Router
import org.scalajs.dom.Element

import scalatags.JsDom.all._

class TagView(tag: String) {

  lazy val color: String = tag.toLowerCase match {
    case "scala"      => "red"
    case "java"       => "teal"
    case "akka"       => "orange"
    case "algorithms" => "blue"
    case _            => ""
  }

  def renderInColor(container: Element): Unit =
    container.appendChild(
      a(`class` := s"ui $color tag label item", onclick := Router.goToTag(tag), tag).render
    )

  def renderInText(container: Element): Unit =
    container.appendChild(
      a(`class` := "item", div(`class` := s"ui $color button", tag), onclick := Router.goToTag(tag)).render
    )
}

object TagView {

  def apply(tag: String) = new TagView(tag)
}
