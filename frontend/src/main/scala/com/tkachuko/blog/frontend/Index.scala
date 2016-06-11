package com.tkachuko.blog.frontend

import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

object Index extends js.JSApp {

  @JSExport
  def main(): Unit = {
    document.body.innerHTML = ""
    List(header, quote, links).foreach(element => document.body.appendChild(element.render))
  }

  def header =
    div(
      `class` := "header",
      div(
        `class` := "home-menu pure-menu pure-menu-horizontal pure-menu-fixed",
        a(`class` := "pure-menu-heading", "Oleksii Tkachuk"),

        ul(
          `class` := "pure-menu-list",
          li(`class` := "pure-menu-item pure-menu-selected", a(href := "/", "Home")),
          li(`class` := "pure-menu-item", a(href := "/blog", `class` := "pure-menu-link", "Blog"))
        )
      )
    )

  def quote =
    div(
      `class` := "splash-container",
      div(
        `class` := "splash",
        h1(`class` := "splash-head", "Simplicity is prerequisite for reliability"),
        p(`class` := "splash-subhead", "Edsger W. Dijkstra"),
        p(a(href := "/blog", `class` := "pure-button pure-button-primary", "Oleksii's programming blog"))
      )
    )

  def links =
    div(
      `class` := "content-wrapper",
      div(
        `class` := "content",
        h2(`class` := "content-head is-center", "Abount me:"),

        div(
          `class` := "pure-g", style := "text-align: center",

          div(
            `class` := "l-box pure-u-1 pure-u-md-1-2 pure-u-lg-1-4",
            h2(
              `class` := "content-subhead",
              i(`class` := "fa fa-linkedin"),
              a(href := "https://www.linkedin.com/in/oleksii-tkachuk-98b47375?trk=hp-identity-name", "Java/Scala Developer")
            )
          ),
          div(
            `class` := "l-box pure-u-1 pure-u-md-1-2 pure-u-lg-1-4",
            h2(
              `class` := "content-subhead",
              i(`class` := "fa fa-tasks"),
              a(href := "https://www.hackerrank.com/kelebra20", "Hacker")
            )
          ),
          div(
            `class` := "l-box pure-u-1 pure-u-md-1-2 pure-u-lg-1-4",
            h2(
              `class` := "content-subhead",
              i(`class` := "fa fa-github"),
              a(href := "https://github.com/kelebra", "Contributor")
            )
          ),
          div(
            `class` := "l-box pure-u-1 pure-u-md-1-2 pure-u-lg-1-4",
            h2(
              `class` := "content-subhead",
              i(`class` := "fa fa-stack-overflow"),
              a(href := "http://stackoverflow.com/users/1299270/tkachuko", "Q&A Fan")
            )
          )
        )
      ),
      shortInfo,
      footer
    )

  def shortInfo =
    div(
      `class` := "ribbon l-box-lrg pure-g",
      div(
        `class` := "l-box-lrg is-center pure-u-1 pure-u-md-1-2 pure-u-lg-2-5",
        img(`class` := "pure-img-responsive", alt := "File Icons", width := "300", src := "/pages/img/profile.png")
      ),
      div(
        `class` := "pure-u-1 pure-u-md-1-2 pure-u-lg-3-5",
        h2(`class` := "content-head content-head-ribbon", "Quick info:"),
        p("Comfortable with ", i("Java"), ",", i("Scala"), " and their framework ecosystem. Programming enthusiast and blogger.")
      )
    )

  def footer = div(`class` := "footer l-box is-center", "2016 Oleksii Tkachuk")
}
