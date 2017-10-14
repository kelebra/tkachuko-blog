package com.tkachuko.blog.service

import akka.actor.{Actor, ActorLogging, Props}
import com.tkachuko.blog.model.{Tag, Title}
import org.scalajs.dom._

package object url {

  type URL = String

  sealed trait PathEvent

  case class PostEvent(title: Title) extends PathEvent

  case class TagEvent(tag: Tag) extends PathEvent

  case object BlogEvent extends PathEvent

  class PersistentUrlActor(initial: URL, affectJs: Boolean = true) extends Actor with ActorLogging {

    override def preStart(): Unit =
      window.addEventListener("popstate", (_: PopStateEvent) => self ! window.location.href)

    def receive: Receive = urlState()

    private def urlState(current: URL = initial): Receive = {
      case url: URL if change(current, url) =>
        sender() ! event(url)
        context.become(urlState(url))
    }

    private def event(current: URL): PathEvent =
      current match {
        case hasPost: URL if hasPost contains post => PostEvent(hasPost.split(post).last)
        case hasTag: URL if hasTag contains tag    => TagEvent(hasTag.split(tag).last)
        case _                                     => BlogEvent
      }

    private def change(current: URL, next: URL): Boolean = {
      val changed = current != next
      if (changed && affectJs) window.location.href = next
      changed
    }
  }

  object PersistentUrlActor {


    def apply: Props = Props(classOf[PersistentUrlActor], window.location.href)

    def apply(initial: URL): Props = Props(classOf[PersistentUrlActor], initial, false)
  }

  private val js = "#"

  private val post = s"${js}post="

  private val tag = s"${js}tag="

}
