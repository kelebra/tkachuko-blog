package com.tkachuko.blog.repository

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Terminated}
import com.tkachuko.blog.repository.source.memory.{InMemorySource, Read}
import com.tkachuko.blog.util.ReadableMillisDifference

import scala.language.postfixOps

trait StaticDataActor[T] extends Actor with ActorLogging {

  def payload: T

  def receive: Receive = forwardToChild()

  def forwardToChild(memory: ActorRef = child): Receive = {
    case Read            =>
      memory forward Read
    case Terminated(ref) =>
      context.unwatch(ref) ! PoisonPill
      context.become(forwardToChild(child))
  }

  private def child = context.watch(context.actorOf(InMemorySource(payload)))
}

object ExperienceActor extends StaticDataActor[Map[String, String]] {

  def payload: Map[String, String] = ("Java", 1325397600000L.readableFromNow()) ::
    ("Scala(.js)", 1388556000000L.readableFromNow()) :: Nil toMap
}

object GithubActor extends StaticDataActor[List[String]] {

  def payload: List[String] = List(
    "https://github.com/kelebra/akka-js-snake",
    "https://github.com/kelebra/programming-interview-java",
    "https://github.com/kelebra/uber-stream-app",
    "https://github.com/kelebra/tkachuko-blog"
  )
}

case class ContactInfo(email: String, photo: String, location: String)

object ContactInfoActor extends StaticDataActor[ContactInfo] {

  def payload: ContactInfo = ContactInfo(
    email = "kelebra20@gmail.com",
    photo = "https://www.facebook.com/photo.php?fbid=1883058935241431&l=d4bae315bd",
    location = "Nashville, TN"
  )
}