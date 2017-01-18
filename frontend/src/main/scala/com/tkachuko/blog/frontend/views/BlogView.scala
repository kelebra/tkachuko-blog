package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.{Post, PostInfo}

import scalatags.JsDom.all._

object BlogView {

  def renderPosts(posts: List[Post]): Unit = render(() => {
    posts.map(PostView(_, posts.tail.isEmpty)).foreach(_.renderIn("posts".byId))
    posts.tags.map(TagView.apply).foreach(_.renderInText("sidebar".byId))
  })

  def renderPostsInfo(infos: List[PostInfo]): Unit = render(() => {
    infos.map(PostInfoView.apply).foreach(_.renderIn(BlogView.posts))
    infos.tags.map(TagView.apply).foreach(_.renderInText(BlogView.sidebar))
  })

  private def render(`content renderer`: () => Unit) = {
    replaceBodyWith(sideBar, staticContent)

    `content renderer`()

    highlightCode()
    renderGraphics()
  }

  private def sideBar =
    div(
      id := "sidebar",
      `class` := "ui sidebar inverted vertical labeled icon menu",
      a(`class` := "item", href := "/", i(`class` := "home icon"), "Home"),
      a(`class` := "item", href := "/blog", i(`class` := "align justify icon"), "Blog"),
      a(`class` := "item", i(`class` := "hashtag icon"), "Posts by tag:")
    ).render

  private def staticContent =
    div(
      `class` := "pusher",
      div(
        `class` := "ui top fixed inverted menu",
        a(`class` := "item", i(`class` := "list layout icon"), "Menu", onclick := onSideBarToggle())
      ),
      br,
      br,
      br,
      div(
        `class` := "ui container",
        div(
          id := "posts",
          `class` := "ui relaxed divided items four stackable cards", style := "margin: 1em"
        )
      )
    ).render

  def posts = "posts".byId

  def sidebar = "sidebar".byId
}
