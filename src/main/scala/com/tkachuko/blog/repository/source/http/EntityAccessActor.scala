package com.tkachuko.blog.repository.source.http

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props, Terminated}
import com.tkachuko.blog.model._
import com.tkachuko.blog.repository.source.http.EntityAccessActor.{Query, request}

abstract class EntityAccessActor(val proto: Option[HttpAccess], collection: String)
  extends Actor with ActorLogging {

  def forwardTo(http: ActorRef): Receive = {
    case query: Query    =>
      http forward request(collection, query)
    case Terminated(ref) =>
      context.unwatch(ref) ! PoisonPill
      context.become(forwardTo(child))
  }

  def receive: Receive = forwardTo(child)

  protected def child: ActorRef

  import context.dispatcher

  protected def resolveAccess: HttpAccess = proto.getOrElse(HttpAccess.native)
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
      path = s"/api/1/databases/blog/collections/$collection$queryParameter$divider$fieldsParameter",
      token = Option(MLabToken)
    )
  }

}

object PostAccessActor extends EntityAccessActor(None, "posts") {

  def child: ActorRef = {

    import io.circe.Decoder
    import io.circe.generic.semiauto._
    implicit val decoder: Decoder[Post] = deriveDecoder[Post]

    context.watch(context.actorOf(HttpAccessActor[Posts](resolveAccess)))
  }

  def apply = Props(this.getClass)

  case class ReadPost(title: String) extends Query {

    override val filter: Option[String] = Option(s"""{"title":"$title"}""")
  }

}

object PostInfoActor extends EntityAccessActor(None, "posts") {

  def child: ActorRef = {

    import io.circe.Decoder
    import io.circe.generic.semiauto._
    implicit val decoder: Decoder[PostInfo] = deriveDecoder[PostInfo]

    context.watch(context.actorOf(HttpAccessActor[Infos](resolveAccess)))
  }

  def apply = Props(this.getClass)

  case object ReadPostInfos extends Query {

    override val fields: Option[String] = Option(s"""{"content":0}""")
  }

}