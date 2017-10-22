package com.tkachuko.blog.repository.source.http

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Terminated}
import com.tkachuko.blog.model._
import com.tkachuko.blog.repository.source.http.EntityAccessActor.{Query, request}
import com.tkachuko.blog.repository.source.http.JsonSupport.Unmarshal

abstract class EntityAccessActor[T](collection: String, access: () => HttpAccess)
                                   (implicit val unmarshal: Unmarshal[T]) extends Actor with ActorLogging {

  import context.dispatcher

  protected def child: ActorRef =
    context.watch(context.actorOf(HttpAccessActor[T](access())))

  private def forwardTo(http: ActorRef): Receive = {
    case query: Query    =>
      http forward request(collection, query)
    case Terminated(ref) =>
      context.unwatch(ref) ! PoisonPill
      context.become(forwardTo(child))
  }

  def receive: Receive = forwardTo(child)
}

object EntityAccessActor {

  trait Query {

    val filter: Option[String] = None

    val fields: Option[String] = None
  }

  def request(collection: String, query: Query): HttpRequest = {
    val queryParameter = query.filter.map(v => s"?q=$v").getOrElse("")
    val fieldsParameter = query.fields.map(v => s"f=$v").getOrElse("")
    val divider = if (query.fields.isEmpty) "" else "?"

    HttpRequest(
      protocol = Protocol.https,
      host = "api.mlab.com",
      path = s"api/1/databases/blog/collections/$collection$queryParameter$divider$fieldsParameter",
      token = Option(MLabToken)
    )
  }

}

import com.tkachuko.blog.repository.source.http.JsonSupport.postUnmarshal

object PostAccessActor extends EntityAccessActor[Posts]("posts", () => Native) {

  case class ReadPost(title: String) extends Query {

    override val filter: Option[String] = Option(s"""{"title":"$title"}""")
  }

}

import com.tkachuko.blog.repository.source.http.JsonSupport.infoUnmarshal

object PostInfoActor extends EntityAccessActor[Infos]("posts", () => Native) {

  case object ReadPostInfos extends Query {

    override val fields: Option[String] = Option(s"""{"_id":0,"content":0}""")
  }

}