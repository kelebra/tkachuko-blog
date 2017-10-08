package com.tkachuko.blog.repository.source

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.pattern.pipe
import com.tkachuko.blog.repository.source.http.Protocol.Protocol
import com.tkachuko.blog.util._

import scala.concurrent.Future
import scalaz.\/

package object http {

  type Host = String
  type Path = String

  type Unmarshal[T] = String => Future[T]

  object Protocol extends Enumeration {
    type Protocol = Value
    val http, https = Value
  }

  sealed case class HttpRequest(protocol: Protocol = Protocol.http,
                                host: Host,
                                path: Path = "",
                                token: Option[Token] = None) {
    def build: String = {
      val baseUrl = s"$protocol://$host/$path"
      val httpTokenParameter = token.fold("")(t => s"&${t.parameter}=${t.value}")
      baseUrl concat httpTokenParameter
    }
  }

  type HttpResponse[T] = \/[Throwable, T]

  class HttpAccessActor[T](httpAccess: HttpAccess,
                           unmarshal: Unmarshal[T]) extends Actor with ActorLogging {

    import context.dispatcher

    def receive: Receive = {
      case request: HttpRequest =>
        pipe(
          httpAccess.send(request)
            .flatMap(unmarshal)
            .andThen { case reply => log.info("{} => {}", request, reply) }
            .<\/>
        ) to sender()
    }
  }

  object HttpAccessActor {

    import io.circe._
    import io.circe.parser._

    def apply[T](httpAccess: HttpAccess)(implicit decoder: Decoder[T]): Props = {
      val jsonDispatcher = (json: String) => decode[T](json).fold(Future.failed, Future.successful)
      Props(classOf[HttpAccessActor[T]], httpAccess, jsonDispatcher)
    }
  }

}
