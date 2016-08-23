package com.tkachuko.blog.frontend.views

import java.util.concurrent.TimeUnit

import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.Post
import org.scalajs.dom.{Element, MouseEvent, document}

import scalatags.JsDom.all._

class PostView(post: Post, tags: List[TagView]) {

  private def daysPublishedAgo =
    math.abs((System.currentTimeMillis() - post.created.toLong) / TimeUnit.DAYS.toMillis(1)).toInt

  def renderIn(container: Element) = {
    val title = post.title
    val tagsElementId = s"tags - $title"

    container.appendChild(
      div(
        `class` := "item",
        div(
          `class` := "content",
          h1(`class` := "ui block header", onclick := onTitleClick(container), title),
          div(
            id := tagsElementId,
            `class` := "meta",
            a(s"Published $daysPublishedAgo day(s) ago")
          ),
          div(id := title, `class` := "description")
        )
      ).render
    )

    tags.foreach(_.renderInColor(document.getElementById(tagsElementId)))

    document.getElementById(title).innerHTML = post.content
  }

  private def onTitleClick(container: Element): MouseEvent => Unit = event => {
    container.innerHTML = ""
    renderIn(container)
    highlightCode()
  }
}

object PostView {

  def apply(post: Post, tags: List[TagView]) = new PostView(post, tags)
}
