package com.tkachuko.blog.view

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import com.tkachuko.blog.service.url.{BlogEvent, IndexEvent, PersistentUrlActor}

object ViewRouter extends Actor with ActorLogging {

  def receive: Receive = viewFromUrlEvent()

  def viewFromUrlEvent(urlActor: ActorRef = urlActor): Receive = {
    case IndexEvent      => context.actorOf(Props(Index)) ! Render
//    case BlogEvent       => context.actorOf(Props(Blog)) ! Render
    case Terminated(ref) =>
      context.unwatch(ref)
      context.become(viewFromUrlEvent())
  }

  private def urlActor = context.watch(context.actorOf(PersistentUrlActor(self)))
}
