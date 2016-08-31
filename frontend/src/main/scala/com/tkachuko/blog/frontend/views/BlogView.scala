package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.Post

import scalatags.JsDom.all._

class BlogView(posts: List[Post]) {

  def render = {
    replaceBodyWith(sideBar, staticContent)

    posts.map(post => PostView(post)).foreach(_.renderIn("posts".byId))
    posts.tags.map(TagView.apply).foreach(_.renderInText("sidebar".byId))

    highlightCode()
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
          `class` := "ui relaxed divided items"
        )
      )
    ).render

}

object BlogView {

  def apply(posts: List[Post]) = new BlogView(posts)
}
