package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.router.Router
import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.{Post, PostInfo}
import scalatags.JsDom.all._

object BlogView {

  def renderPosts(posts: List[Post]): Unit = render(() => {
    posts.map(PostView(_, posts.tail.isEmpty)).foreach(_.renderIn("posts".byId))
  })

  def renderPostsInfo(infos: List[PostInfo]): Unit = render(() => {
    infos.map(PostInfoView.apply).foreach(_.renderIn(BlogView.posts))
  })

  private def render(`content renderer`: () => Unit): Unit = {
    replaceBodyWith(sideBar, staticContent)

    `content renderer`()

    highlightCode()
    renderGraphics()
  }

  private def sideBar =
    div(
      id := "sidebar",
      `class` := "ui fixed inverted menu",
      div(`class` := "ui container",
        a(`class` := "header item", onclick := Router.goHome,
          img(
            `class` := "logo",
            style := "margin-right: 1.5em",
            src := "https://i.ibb.co/pvbWLtR/logo-transparent.png"
          ),
          "Home"
        ),
        a(`class` := "item", onclick := Router.goToBlog, i(`class` := "align justify icon"), "Blog")
      )
    ).render

  private def staticContent =
    div(
      `class` := "ui main container",
      style := "margin-top: 7em",
      div(
        id := "posts",
        `class` := "ui relaxed divided items four stackable cards", style := "margin: 1em"
      )
    ).render

  def posts = "posts".byId

  def sidebar = "sidebar".byId
}
