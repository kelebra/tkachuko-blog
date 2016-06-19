package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.Elements._
import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.Post
import org.scalajs.dom._

import scalatags.JsDom.all._

class BlogView(posts: List[PostView]) {

  def render = {
    document.body.innerHTML = ""
    document.body.appendChild(staticContent())
    posts.foreach(_.renderIn(document.getElementById("posts")))
    highlightCode()
  }

  private def staticContent() =
    div(
      `class` := "pure-g",
      div(
        `class` := "sidebar pure-u-1 pure-u-md-1-4",
        div(
          `class` := "header",

          h1(`class` := "brand-title", "Oleksii's blog"),

          h2(`class` := "brand-tagline", "Technical blog dedicated to java and scala"),

          nav(
            `class` := "nav",

            ul(
              `class` := "nav-list",

              li(`class` := "nav-item", a(`class` := "pure-button", href := "/", "Home")),
              li(`class` := "nav-item", a(`class` := "pure-button", href := "/blog", "Blog"))
            )
          )
        )
      ),
      div(
        `class` := "content pure-u-1 pure-u-md-3-4",
        div(
          id := "posts",
          `class` := "posts",
          h1(id := "heading", `class` := "content-subhead", "All posts")
        )
      )
    ).render
}

object BlogView {

  def apply(posts: List[Post]) = new BlogView(posts.map(PostView.apply))
}
