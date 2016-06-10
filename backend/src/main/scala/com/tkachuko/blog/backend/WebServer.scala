package com.tkachuko.blog.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.tkachuko.blog.backend.json.JsonSupport._
import com.tkachuko.blog.backend.static.StaticDataResolver._
import com.tkachuko.blog.db.Database
import com.tkachuko.blog.models.Subscription
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
          complete(Database.Posts.all().map(_.sortBy(-_.created).toJson))
        } ~
        path(postByTitle / Rest) { title =>
          complete(Database.Posts.findByTitle(title).map(_.toJson))
        } ~
        path(subscribe / Rest) { email =>
          complete(
            Database.Subscriptions.insert(Subscription(email)).map(_.ok.toString)
          )
        } ~
        getFromResourceDirectory(".")
    } ~
      post {
        path(postsByTags) {
          entity(as[String]) { comaSeparatedTags =>
            val tags = comaSeparatedTags.split(",").map(_.trim).toList
            complete(
              Database.Posts.findByTags(tags).map(_.toJson)
            )
          }
        }
      }

  def main(args: Array[String]): Unit = {
    val binding = Http().bindAndHandle(routes, args(0), args(1).toInt)

    binding.onFailure {
      case e: Exception =>
        println(e)
        system.terminate()
    }
  }
}
