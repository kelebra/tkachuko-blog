package com.tkachuko.blog.view

import akka.actor.Props
import akka.pattern.ask
import com.tkachuko.blog.model.{Infos, PostInfo, Tag}
import com.tkachuko.blog.repository.source.http.EntityAccessActor.Query
import com.tkachuko.blog.repository.source.http.PostInfoActor
import com.tkachuko.blog.repository.source.http.PostInfoActor.ReadPostInfos
import com.tkachuko.blog.util.ReadableMillisDifference

import scalatags.JsDom.all._
import scalaz.{-\/, \/, \/-}

class Blog(query: Query) extends ViewActor {

  import context.dispatcher

  def view: DivView = for {
    infoLoad <- (context.actorOf(Props(PostInfoActor)) ? ReadPostInfos).mapTo[\/[Throwable, Infos]]
  } yield infoLoad match {
    case \/-(infos) => div(sideBar(infos.flatMap(_.tags).toSeq: _*), blogContent(infos.toSeq: _*))
    case -\/(error) => throw error
  }

  private def blogContent(infos: PostInfo*) =
    div(
      `class` := "pusher",
      div(
        `class` := "ui top fixed inverted menu",
        a(`class` := "item", i(`class` := "list layout icon"), "Menu")
      ),
      br,
      br,
      br,
      div(
        `class` := "ui container",
        div(
          id := "posts",
          `class` := "ui relaxed divided items four stackable cards", style := "margin: 1em",
          infos.sortBy(-_.created).map(info => postInfo(info, info.tags.toSeq: _*))
        )
      )
    )

  private def sideBar(tags: Tag*) =
    div(
      id := "sidebar",
      `class` := "ui sidebar inverted vertical labeled icon menu",
      a(`class` := "item", href := "/", i(`class` := "home icon"), "Home"),
      a(`class` := "item", href := "/blog", i(`class` := "align justify icon"), "Blog"),
      a(`class` := "item", i(`class` := "hashtag icon"), "Posts by tag:"),
      tags.map(tagView)
    )

  // TODO: title click
  private def postInfo(info: PostInfo, tags: Tag*) =
    div(`class` := "ui card",
      div(`class` := "content",
        a(`class` := "ui block header", info.title),
        div(`class` := "meta", span(s"Published ${info.created.toLong.readableFromNow()} ago"))
      ),
      div(`class` := "extra content", tags.map(tagView))
    )

  // TODO: on tag click
  private def tagView(tag: Tag) =
    a(`class` := s"ui ${tagColor(tag)} tag label item", tag)

  private def tagColor(tag: Tag) = tag match {
    case "scala"      => "red"
    case "java"       => "teal"
    case "akka"       => "orange"
    case "algorithms" => "blue"
    case _            => ""
  }
}

object Blog {

  def apply(query: Query = ReadPostInfos): Props = Props(classOf[Blog], query)
}
