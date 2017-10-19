package com.tkachuko.blog.service

import akka.actor.{Actor, ActorLogging, Props}
import com.tkachuko.blog.view.{Blog, Index, Post}
import org.scalajs.dom._

import scala.util.Try

package object url {

  type URL = String

  class PersistentUrlActor(initial: URL, affectJs: Boolean) extends Actor with ActorLogging {

    override def preStart(): Unit =
      window.addEventListener("popstate", (_: PopStateEvent) => self ! window.location.href)

    def receive: Receive = urlState()

    private def urlState(current: URL = initial): Receive = {
      case url: URL if change(current, url) =>
        val props = viewProps(url)
        log.info("Rendering  {}", props)
        context.become(urlState(url))
    }

    private def viewProps(current: URL): Props =
      current match {
        case hasPost: URL if hasPost has post => Post(hasPost.split(post).last)
        case hasTag: URL if hasTag has tag    => ???
        case hasBlog: URL if hasBlog has blog => Blog()
        case _                                => Props(Index)
      }

    private def change(current: URL, next: URL): Boolean = {
      val changed = current != next
      if (changed && affectJs) window.location.href = next
      changed
    }
  }

  object PersistentUrlActor {

    def apply(): Props = Props(classOf[PersistentUrlActor], window.location.href, true)

    def apply(initial: URL): Props = Props(classOf[PersistentUrlActor], initial, false)
  }

  private val js = "#"

  private val post = s"${js}post="

  private val tag = s"${js}tag="

  private val blog = "/blog/"

  private implicit class SafeUrlOrations(url: URL) {

    def has(other: URL): Boolean = Try(url.contains(other)).getOrElse(false)
  }

}
