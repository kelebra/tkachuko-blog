package com.tkachuko.blog.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.tkachuko.blog.backend.rest.RestService

import scala.concurrent.Future

object WebServer {

  implicit val system = ActorSystem("tkachuko-web-system")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  def main(args: Array[String]): Unit = {
    val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
      Http().bind(args(0), args(1).toInt)

    val binding: Future[Http.ServerBinding] =
      serverSource.to(Sink.foreach(_.handleWith(RestService(system).routes))).run

    binding
      .onFailure {
        case any: Any =>
          system.log.error(s"Could not proceed because of exception: $any")
          system.terminate()
      }
  }
}
