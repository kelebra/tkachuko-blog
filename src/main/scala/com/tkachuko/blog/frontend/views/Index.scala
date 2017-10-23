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
          `class` := "ui huge primary button",
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
          `class` := "ui three stackable cards",
          experience,
          info,
          github
        )
      )
    )

  def experience =
    div(
      `class` := "card",
      div(
        `class` := "content",
        div(`class` := "header", "Comfortable with:"),
        div(
          `class` := "description",
          div(
            `class` := "ui centered list",
            div(
              `class` := "item",
              i(`class` := "right aligned battery full icon"),
              div(`class` := "content", s"Java (${1325397600000.0.readableDifference} experience)")
            ),
            div(
              `class` := "item",
              i(`class` := "right aligned battery high icon"),
              div(`class` := "content", s"Scala (${1388556000000.0.readableDifference} experience)")
            )
          )
        )
      ),
      div(`class` := "extra content", i(`class` := "check icon"), "Learn hard, have fun")
    )

  def info =
    div(
      `class` := "card",
      div(
        `class` := "image",
        img(src := "https://media-exp2.licdn.com/mpr/mpr/shrinknp_400_400/AAEAAQAAAAAAAAtaAAAAJDZjYzBjN2ViLTI4ZDQtNDQzNi1iMjU1LWJlYzhiZjcxMmZiNA.jpg")
      ),
      div(
        `class` := "content",
        div(`class` := "header", "Oleksii Tkachuk"),
        div(`class` := "meta", a("Software Developer"))
      ),
      div(
        `class` := "extra content",
        a(href := "mailto:kelebra20@gmail.com", `class` := "left floated", i(`class` := "mail icon"), "kelebra20@gmail.com"),
        span(`class` := "right floated", i(`class` := "marker icon"), "Nashville, TN")
      )
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
        div(`class` := "header", "Github projects:"),
        div(`class` := "description",
          div(`class` := "ui centered divided list",
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
