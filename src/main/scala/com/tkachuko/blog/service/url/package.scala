package com.tkachuko.blog.service

import akka.actor.{Actor, ActorLogging, Props}
import com.tkachuko.blog.model.{Tag, Title}
import com.tkachuko.blog.view.{Blog, Index, Post, Render}
import org.scalajs.dom._

import scala.util.Try

package object url {

  type URL = String

  object UrlActor extends Actor with ActorLogging {

    override def preStart(): Unit =
      window.addEventListener("popstate", (_: PopStateEvent) => self ! window.location.hash)

    def receive: Receive = renderAddressBar andThen viewProps andThen render

    private val render: PartialFunction[Props, Unit] = {
      case props: Props =>
        log.info("Rendering {}", props.clazz.getSimpleName)
        context.actorOf(props) ! Render
    }

    private val viewProps: PartialFunction[Any, Props] = {
      case ""                                 => Props(Index)
      case hasPost: URL if hasPost has post() => Post(hasPost.split(post()).last)
      case hasTag: URL if hasTag has tag()    => ???
      case hasBlog: URL if hasBlog has blog   => Blog()
      case _                                  => Props(Index)
    }

    private val renderAddressBar: PartialFunction[Any, Any] = {
      case change if change != window.location.hash => window.location.hash = change.toString; change
      case other                                    => other
    }
  }

  private val js = "#"

  private val blog = s"$js/blog"

  def post(title: Title = "") = s"$blog/post/$title"

  def tag(t: Tag = "") = s"$blog/tag/$t"

  def gotoBlog: MouseEvent => Unit = _ => window.location.hash = blog

  private implicit class SafeUrlOrations(url: URL) {

    def has(other: URL): Boolean = Try(url.contains(other)).getOrElse(false)
  }

}
