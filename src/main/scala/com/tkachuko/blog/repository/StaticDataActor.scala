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

case class Experience(language: String, duration: String)

object ExperienceActor extends StaticDataActor[List[Experience]] {

  def payload: List[Experience] =
    Experience("Java", 1325397600000L.readableFromNow()) ::
      Experience("Scala(.js)", 1388556000000L.readableFromNow()) :: Nil
}

case class Repository(url: String)

object GithubActor extends StaticDataActor[List[Repository]] {

  def payload: List[Repository] = List(
    Repository("https://github.com/kelebra/akka-js-snake"),
    Repository("https://github.com/kelebra/programming-interview-java"),
    Repository("https://github.com/kelebra/uber-stream-app"),
    Repository("https://github.com/kelebra/tkachuko-blog")
  )
}

case class ContactInfo(name: String, position: String, email: String, photo: String, location: String)

object ContactInfoActor extends StaticDataActor[ContactInfo] {

  def payload: ContactInfo = ContactInfo(
    name = "Oleksii Tkachuk",
    position = "Software Engineer",
    email = "kelebra20@gmail.com",
    photo = "https://content-na.drive.amazonaws.com/cdproxy/templink/joZ7Hti3ZVLVXIamJ15muCpKrba0d1kzBM07pYlor8QeJxFPc?viewBox=2560%2C1702",
    location = "Nashville, TN"
  )
}

case class Quote(content: String, author: String)

object QuoteActor extends StaticDataActor[Quote] {

  def payload: Quote = Quote("Simplicity is prerequisite for reliability", "Edsger W. Dijkstra")
}