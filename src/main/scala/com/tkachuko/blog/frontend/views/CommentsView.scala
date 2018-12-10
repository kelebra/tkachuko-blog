package com.tkachuko.blog.frontend.views

import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Div, Script}
import scalatags.JsDom.all._

class CommentsView {

  def render(container: Element) = {
    container.appendChild(commentsContainer)
    container.appendChild(setup)
  }

  private def commentsContainer: Div =
    div(id := "commento", style := "width: 100%").render

  private def setup: Script =
    script(src := "https://cdn.commento.io/js/commento.js").render
}

object CommentsView {

  lazy val noscript = typedTag[dom.html.Head]("noscript")

  def apply(): CommentsView = new CommentsView()
}

