package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.Elements._
import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.Post
import org.scalajs.dom.{Element, MouseEvent, document}

import scalatags.JsDom.all._

class PostView(val post: Post) {

  def renderIn(container: Element) = {
    val contentElementId = post.title

    container.appendChild(
      section(
        `class` := "post",
        header(
          `class` := "post-header",
          h2(
            `class` := "post-title",
            style := "cursor: pointer;",
            post.title,
            onclick := onTitleClick(container)
          ),
          p(
            `class` := "post-meta",
            post.tags.map(tag => a(`class` := "post-category post-category-design", tag, onclick := TagView.onTagClick(tag)))
          )
        ),
        div(`class` := "post-description", id := contentElementId)
      ).render
    )

    document.getElementById(contentElementId).innerHTML = post.content
  }

  def onTitleClick(container: Element): MouseEvent => Unit = event => {
    container.innerHTML = ""
    renderIn(container)
    highlightCode()
  }
}

object PostView {

  def apply(post: Post) = new PostView(post)
}
