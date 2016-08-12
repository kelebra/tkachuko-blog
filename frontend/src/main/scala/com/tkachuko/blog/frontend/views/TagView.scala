package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.router.Router
import com.tkachuko.blog.frontend.router.Router._
import org.scalajs.dom.{Element, MouseEvent}

import scalatags.JsDom.all._

class TagView(val tag: String) {

  def renderIn(container: Element): Unit = {
    container.appendChild(
      li(
        `class` := "nav-item",
        a(`class` := "pure-button", onclick := TagView.onTagClick(tag), tag)
      ).render
    )
  }
}

object TagView {

  def apply(tag: String) = new TagView(tag)

  def onTagClick(tag: String): MouseEvent => Unit = event => Router(tag.toTagUrl)
}
