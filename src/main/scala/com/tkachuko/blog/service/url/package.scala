package com.tkachuko.blog.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.tkachuko.blog.model.{Tag, Title}
import org.scalajs.dom._

import scala.util.Try

package object url {

  type URL = String

  sealed trait PathEvent

  case class PostEvent(title: Title) extends PathEvent

  case class TagEvent(tag: Tag) extends PathEvent

  case object BlogEvent extends PathEvent

  case object IndexEvent extends PathEvent

  class PersistentUrlActor(initial: URL,
                           subscriber: ActorRef,
                           affectJs: Boolean) extends Actor with ActorLogging {

    override def preStart(): Unit =
      window.addEventListener("popstate", (_: PopStateEvent) => self ! window.location.href)

    def receive: Receive = urlState()

    private def urlState(current: URL = initial): Receive = {
      case url: URL if change(current, url) =>
        log.info("Moving from {} to {}", current, url)
        subscriber ! event(url)
        context.become(urlState(url))
    }

    private def event(current: URL): PathEvent =
      current match {
        case hasPost: URL if hasPost has post => PostEvent(hasPost.split(post).last)
        case hasTag: URL if hasTag has tag    => TagEvent(hasTag.split(tag).last)
        case hasBlog: URL if hasBlog has blog => BlogEvent
        case _                                => IndexEvent
      }

    private def change(current: URL, next: URL): Boolean = {
      val changed = current != next
      if (changed && affectJs) window.location.href = next
      changed
    }
  }

  object PersistentUrlActor {

    def apply(subscriber: ActorRef): Props =
      Props(classOf[PersistentUrlActor], window.location.href, subscriber, true)

    def apply(initial: URL, subscriber: ActorRef): Props =
      Props(classOf[PersistentUrlActor], initial, subscriber, false)
  }

  private val js = "#"

  private val post = s"${js}post="

  private val tag = s"${js}tag="

  private val blog = "/blog/"

  private implicit class SafeUrlOrations(url: URL) {

    def has(other: URL): Boolean = Try(url.contains(other)).getOrElse(false)
  }

}
