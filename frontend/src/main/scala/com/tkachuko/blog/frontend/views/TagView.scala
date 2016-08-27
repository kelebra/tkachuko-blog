package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.router.Router
import com.tkachuko.blog.frontend.router.Router._
import org.scalajs.dom.{Element, MouseEvent}

import scalatags.JsDom.all._

class TagView(tag: String) {

  lazy val color = tag.toLowerCase match {
    case "scala" => "red"
    case "java" => "teal"
    case "akka" => "orange"
    case "algorithms" => "blue"
    case _ => ""
  }

  def renderInColor(container: Element): Unit =
    container.appendChild(
      a(`class` := s"ui $color tag label item", onclick := TagView.onTagClick(tag), tag).render
    )

  def renderInText(container: Element): Unit =
    container.appendChild(
      a(`class` := "item", div(`class` := s"ui $color button", tag), onclick := TagView.onTagClick(tag)).render
  )
}

object TagView {

  def apply(tag: String) = new TagView(tag)

  def onTagClick(tag: String): MouseEvent => Unit = event => Router(tag.toTagUrl)
}
