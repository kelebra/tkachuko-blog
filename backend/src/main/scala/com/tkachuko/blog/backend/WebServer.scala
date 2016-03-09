package com.tkachuko.blog.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.tkachuko.blog.backend.json.PostJsonSupport._
import com.tkachuko.blog.backend.static.StaticDataResolver._
import com.tkachuko.blog.db.Database
import spray.json._

object WebServer {

  implicit val system = ActorSystem("tkachuko-system")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  val routes =
    get {
      pathSingleSlash {
        homePage
      } ~
        path(resourcePrefix / Rest) { resource =>
          resource.asWebResource
        } ~
        path(blog) {
          blogPage
        } ~
        path(posts) {
          complete(Database.Posts.findAllModels().toJson)
        }
    }

  def main(args: Array[String]): Unit = {
    val binding = Http()
      .bindAndHandle(routes, args(0), args(1).toInt)

    binding
      .onFailure { case e: Exception =>
      println(e)
      system.terminate()
    }
  }
}
