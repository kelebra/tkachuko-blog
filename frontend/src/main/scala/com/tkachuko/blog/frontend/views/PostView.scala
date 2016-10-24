package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.markdown.MarkdownString
import com.tkachuko.blog.frontend.router.Router
import com.tkachuko.blog.frontend.router.Router.URLOps
import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.Post
import org.scalajs.dom.{Element, MouseEvent}

import scalatags.JsDom.all._

class PostView(post: Post, comments: Boolean) {

  def renderIn(container: Element) = {
    val title = post.title
    val tagsElementId = s"tags - $title"
    val postUrl = title.postUrl

    container.appendChild(
      div(
        `class` := "item",
        div(
          `class` := "content",
          a(`class` := "ui block header", href := postUrl, onclick := onTitleClick(postUrl), h1(title)),
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

    if (comments) CommentsView(title).render(container)
  }

  private def onTitleClick(url: String): MouseEvent => Unit = event => Router(url)
}

object PostView {

  def apply(post: Post, comments: Boolean) = new PostView(post, comments)
}
