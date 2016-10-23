package com.tkachuko.blog.db.actor

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import com.tkachuko.blog.db.internal.Database
import com.tkachuko.blog.db.namespace.PostsRequestHandler

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Actor to support remote database queries from various sources.
  */
class DbActor extends Actor with ActorLogging {

  def receive: Receive = PostsRequestHandler(new Database.Posts()) andThen (result => pipe(result) to sender())
}

object DbActor {

  def local(implicit system: ActorSystem) = system.actorOf(props)

  def props = Props(classOf[DbActor])
}
