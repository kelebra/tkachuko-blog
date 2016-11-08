package com.tkachuko.blog.backend.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import com.tkachuko.blog.backend.connector.RemoteDaoConnector
import com.tkachuko.blog.backend.json.JsonSupport
import com.tkachuko.blog.backend.static.StaticDataResolver._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

class RestService(val system: ActorSystem) extends Directives with JsonSupport with RemoteDaoConnector {

  val routes =
    get {
      pathSingleSlash {
        log.info("Returning home page")
        homePage
      } ~
        path(resourcePrefix / Rest) { resource =>
          resource.webResource
        } ~
        path(frontend) {
          frontendJs
        } ~
        path(blog ~ Slash.? ~ Rest.?) { _ =>
          log.info("Returning blog page")
          blogPage
        } ~
        path(posts) {
          log.info("Requesting all posts")
          complete(allPosts)
        } ~
        path(postByTitle / Rest) { title =>
          log.info(s"Requesting for post with title '$title'")
          complete(findPostsByTitle(title.withoutHttpSpaces))
        } ~
        path(posts / count) {
          complete(postsCount.map(_.toJson))
        }
    } ~
      post {
        path(postsByTags) {
          entity(as[String]) { tags =>
            log.info(s"Requesting posts with tags '$tags'")
            complete(findPostsByTags(tags.comaSeparatedList))
          }
        }
      }

  implicit class URLOps(value: String) {

    def withoutHttpSpaces = value.replace("%20", " ")

    def comaSeparatedList = value.split(",").map(_.trim).toList
  }

}

object RestService {

  def apply(system: ActorSystem) = new RestService(system)
}