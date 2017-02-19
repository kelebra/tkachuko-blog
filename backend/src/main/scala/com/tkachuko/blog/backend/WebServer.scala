package com.tkachuko.blog.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.tkachuko.blog.backend.rest.RestService
import com.tkachuko.blog.db.internal.Database

object WebServer {

  implicit val system = ActorSystem("tkachuko-web-system")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  def main(args: Array[String]): Unit = {
    val (host, port) = (args(0), args(1).toInt)
    val serverSource = Http().bind(host, port)
    val routes = RestService.routes(new Database.Posts(), new Database.PostsInfo())
    val binding = serverSource.to(Sink.foreach(_.handleWith(routes))).run

    binding
      .onFailure {
        case any: Any =>
          system.log.error(s"Could not proceed because of exception: $any")
          system.terminate()
      }
  }
}
