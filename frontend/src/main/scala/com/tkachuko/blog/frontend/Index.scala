package com.tkachuko.blog.frontend

import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

object Index extends js.JSApp {

  @JSExport
  def main(): Unit = {
    document.body.innerHTML = ""
    document.body.appendChild(page.render)
  }

  def page = div(`class` := "pusher", header, body)

  def header =
    div(
      `class` := "ui inverted vertical masthead center aligned segment",
      div(
        `class` := "ui container",
        div(
          `class` := "ui large secondary inverted pointing menu",
          a(`class` := "active item", "Home"),
          a(`class` := "item", href := "/blog", "Blog")
          //          TODO: create CV page
          //          a(`class` := "item", "CV")
        )
      ),
      div(
        `class` := "ui text container",
        h1(`class` := "ui inverted header", "Simplicity is prerequisite for reliability"),
        h2("Edsger W. Dijkstra"),
        a(
          `class` := "ui huge primary button",
          href := "/blog",
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
          `class` := "ui link cards",
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
            `class` := "ui centered animated list",
            div(
              `class` := "item",
              i(`class` := "right aligned battery full icon"),
              div(`class` := "content", "Java (4+ years experience)")
            ),
            div(
              `class` := "item",
              i(`class` := "right aligned battery high icon"),
              div(`class` := "content", "Scala (2+ years experience)")
            ),
            div(
              `class` := "item",
              i(`class` := "right aligned battery medium icon"),
              div(`class` := "content", "Python (2+ years experience)")
            ),
            div(
              `class` := "item",
              i(`class` := "right aligned battery low icon"),
              div(`class` := "content", "JavaScipt (1+ years experience)")
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
        img(src := "http://cs631616.vk.me/v631616937/317ce/9lMzOM5RK18.jpg")
      ),
      div(
        `class` := "content",
        div(`class` := "header", "Oleksii Tkachuk"),
        div(`class` := "meta", a("Software Developer")),
        div(`class` := "description", "Oleksii Tkachuk")
      ),
      div(
        `class` := "extra content",
        span(`class` := "left floated", i(`class` := "mail icon"), "kelebra20@gmail.com"),
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
          div(`class` := "ui centered divided animated list",
            repo("https://github.com/kelebra/chess-challenge"),
            repo("https://github.com/kelebra/programming-interview-java"),
            repo("https://github.com/kelebra/security-identifier"),
            repo("https://github.com/kelebra/tkachuko-blog")
          )
        )
      ),
      div(`class` := "extra content", i(`class` := "check icon"), "Code hard, have fun")
    )
  }
}
