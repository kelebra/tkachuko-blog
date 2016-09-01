package com.tkachuko.blog.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.tkachuko.blog.backend.json.JsonSupport
import com.tkachuko.blog.backend.static.StaticDataResolver._
import com.tkachuko.blog.db.Database
import com.tkachuko.blog.models.Subscription
import spray.json._

import scala.concurrent.Future

object WebServer {

  implicit val system = ActorSystem("tkachuko-system")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  object RestService extends Directives with JsonSupport {
    val routes =
      get {
        pathSingleSlash {
          homePage
        } ~
          path(resourcePrefix / Rest) { resource =>
            resource.asWebResource
          } ~
          path(frontend) {
            frontendJs
          } ~
          path(blog ~ Slash.? ~ Rest.?) { _ =>
            blogPage
          } ~
          path(posts) {
            complete(Database.Posts.all())
          } ~
          path(postByTitle / Rest) { title =>
            complete(Database.Posts.findByTitle(title.withoutHttpSpaces))
          } ~
          path(posts / count) {
            complete(Database.Posts.count().map(_.toJson))
          } ~
          path(subscribe / Rest) { email =>
            complete(Database.Subscriptions.insert(Subscription(email)).map(_.ok.toString))
          }
      } ~
        post {
          path(postsByTags) {
            entity(as[String]) { tags =>
              complete(Database.Posts.findByTags(tags.comaSeparatedList))
            }
          }
        }
  }

  def main(args: Array[String]): Unit = {
    val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
      Http().bind(args(0), args(1).toInt)

    val binding: Future[Http.ServerBinding] =
      serverSource.to(Sink.foreach(_.handleWith(RestService.routes))).run

    binding.onFailure {
      case e: Exception =>
        println(e)
        system.terminate()
    }
  }
}
