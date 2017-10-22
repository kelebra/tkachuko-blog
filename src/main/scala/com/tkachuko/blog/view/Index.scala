package com.tkachuko.blog.view

import akka.actor.Props
import akka.pattern.ask
import com.tkachuko.blog.repository._
import com.tkachuko.blog.repository.source.memory.Read
import com.tkachuko.blog.service.url.gotoBlog

import scalatags.JsDom.all._

object Index extends ViewActor {

  import context.dispatcher

  override protected def view: DivView = {

    for {
      quote <- (context.actorOf(Props(QuoteActor)) ? Read).mapTo[Quote]
      exp <- (context.actorOf(Props(ExperienceActor)) ? Read).mapTo[List[Experience]]
      contact <- (context.actorOf(Props(ContactInfoActor)) ? Read).mapTo[ContactInfo]
      repos <- (context.actorOf(Props(GithubActor)) ? Read).mapTo[List[Repository]]
    } yield div(`class` := "pusher", header(quote), body(exp, contact, repos))
  }

  private def header(quote: Quote) =
    div(
      `class` := "ui inverted vertical masthead center aligned segment",
      div(
        `class` := "ui text container",
        h1(`class` := "ui inverted header", quote.content),
        h2(quote.author),
        a(
          `class` := "ui huge primary button",
          "Welcome to my blog",
          onclick := gotoBlog,
          i(`class` := "right arrow icon")
        )
      )
    )

  private def body(exp: List[Experience],
                   cnt: ContactInfo,
                   rps: List[Repository]) =
    div(
      `class` := "ui vertical stripe segment",
      div(
        `class` := "ui middle aligned centered stackable grid container",
        div(
          `class` := "ui three stackable cards",
          experience(exp: _*),
          info(cnt),
          github(rps: _*)
        )
      )
    )

  private def languageExperience(experience: Experience) =
    div(
      `class` := "item",
      i(`class` := "right aligned battery full icon"),
      div(`class` := "content", s"${experience.language} (${experience.duration} experience)")
    )

  private def experience(data: Experience*) =
    div(
      `class` := "card",
      div(
        `class` := "content",
        div(`class` := "header", "Comfortable with:"),
        div(
          `class` := "description",
          div(
            `class` := "ui centered list",
            data.map(languageExperience)
          )
        )
      ),
      div(`class` := "extra content", i(`class` := "check icon"), "Learn hard, have fun")
    )

  private def info(contactInfo: ContactInfo) =
    div(
      `class` := "card",
      div(
        `class` := "image",
        img(src := s"${contactInfo.photo}")
      ),
      div(
        `class` := "content",
        div(`class` := "header", contactInfo.name),
        div(`class` := "meta", a(contactInfo.position))
      ),
      div(
        `class` := "extra content",
        a(
          href := s"mailto:${contactInfo.email}",
          `class` := "left floated",
          i(`class` := "mail icon"),
          contactInfo.email
        ),
        span(`class` := "right floated", i(`class` := "marker icon"), contactInfo.location)
      )
    )

  def repo(url: String) =
    div(`class` := "item",
      i(`class` := "large github right aligned icon"),
      div(`class` := "content",
        a(`class` := "header", href := url, url.substring(url.lastIndexOf("/") + 1))
      )
    )

  private def github(repositories: Repository*) =
    div(
      `class` := "ui card",
      div(
        `class` := "content",
        div(`class` := "header", "Github projects:"),
        div(`class` := "description",
          div(`class` := "ui centered divided animated list", repositories.map(_.url).map(repo))
        )
      ),
      div(`class` := "extra content", i(`class` := "check icon"), "Code hard, have fun")
    )
}