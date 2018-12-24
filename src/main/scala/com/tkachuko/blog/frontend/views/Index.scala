package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.router.Router
import com.tkachuko.blog.frontend.util.Util
import com.tkachuko.blog.frontend.util.Util.MillisToReadableDifference

import scalatags.JsDom.all._

object Index {

  def render(): Unit = Util.replaceBodyWith(page.render)

  def page = div(`class` := "pusher", header, body)

  def header =
    div(
      `class` := "ui inverted vertical masthead center aligned segment quote",
      div(
        `class` := "ui text container",
        h1(`class` := "ui inverted header", "Simplicity is prerequisite for reliability"),
        h2("Edsger W. Dijkstra"),
        a(
          `class` := "ui inverted huge primary button",
          onclick := Router.goToBlog,
          "Welcome to my blog",
          i(`class` := "right arrow icon")
        )
      )
    )

  def body =
    div(
      `class` := "ui vertical stripe segment",
      div(
        `class` := "ui middle aligned centered stackable grid container",
        div(
          `class` := "ui two stackable cards",
          info,
          github
        )
      )
    )

  def info =
    div(
      `class` := "card",
      div(
        `class` := "image",
        img(src := "https://i.ibb.co/X2dTJv2/DSC-5307.jpg")
      ),
      div(
        `class` := "content",
        div(`class` := "header", "Oleksii Tkachuk"),
        div(`class` := "meta", a("Software Engineer"))
      ),
      div(`class` := "extra content", i(`class` := "check icon"), a(href := "https://www.dropbox.com/s/u6708doh9qr0fc1/Oleksii%20Tkachuk-CV.pdf?dl=0", "Download CV"))
    )

  def github = {
    def repo(url: String) =
      div(`class` := "item",
        i(`class` := "large github right aligned icon"),
        div(`class` := "content", a(`class` := "header", href := url, url.substring(url.lastIndexOf("/") + 1)))
      )

    div(
      `class` := "ui card",
      div(
        `class` := "content",
        div(`class` := "description",
          div(`class` := "ui relaxed divided list",
            repo("https://github.com/kelebra/akka-js-snake"),
            repo("https://github.com/kelebra/programming-interview-java"),
            repo("https://github.com/kelebra/uber-stream-app"),
            repo("https://github.com/kelebra/tkachuko-blog")
          )
        )
      ),
      div(`class` := "extra content", i(`class` := "check icon"), "Code hard, have fun")
    )
  }
}
