package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.markdown.MarkdownString
import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.Post
import org.scalajs.dom.Element

import scalatags.JsDom.all._

class PostView(post: Post, comments: Boolean) {

  def renderIn(container: Element): Unit = {
    val title = post.title
    val tagsElementId = s"tags - $title"

    container.appendChild(
      div(
        `class` := "item",
        div(
          `class` := "content",
          div(`class` := "ui block header", h1(title)),
          div(
            id := tagsElementId,
            `class` := "meta",
            span(s"Published ${post.created.readableDifference} ago")
          ),
          div(id := title, `class` := "description")
        )
      ).render
    )

    post.tags.map(TagView.apply).foreach(_.renderInColor(tagsElementId.byId))
    replaceElementContent(title.byId, post.content.md, br.render)

    if (comments) CommentsView().render(container)
  }
}

object PostView {

  def apply(post: Post, comments: Boolean) = new PostView(post, comments)
}
