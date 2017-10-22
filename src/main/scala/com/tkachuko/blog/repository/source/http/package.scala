package com.tkachuko.blog.repository.source

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import com.tkachuko.blog.repository.source.http.JsonSupport.Unmarshal
import com.tkachuko.blog.repository.source.http.Protocol.Protocol
import com.tkachuko.blog.util._

import scalaz.\/

package object http {

  type Host = String
  type Path = String

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
            .map(unmarshal)
            .andThen { case reply => log.info("HTTP reply of type {}", reply.getClass.getSimpleName) }
            .<\/>
        ) to sender()
    }
  }

  object HttpAccessActor {

    def apply[T](httpAccess: HttpAccess)(implicit unmarshal: Unmarshal[T]): Props =
      Props(classOf[HttpAccessActor[T]], httpAccess, unmarshal)
  }

}
