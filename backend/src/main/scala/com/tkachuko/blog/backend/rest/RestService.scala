package com.tkachuko.blog.backend.rest

import java.net.URLDecoder

import akka.http.scaladsl.server.{Directives, Route}
import com.tkachuko.blog.backend.json.JsonSupport
import com.tkachuko.blog.backend.static.StaticDataResolver._
import com.tkachuko.blog.db.repository.{PostInfoRepository, PostRepository}
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

class RestService(`post repository`: => PostRepository,
                  `info repository`: => PostInfoRepository) extends Directives with JsonSupport {

  val routes: Route =
    get {
      pathSingleSlash {
        homePage
      } ~
        path(resourcePrefix / Remaining) { resource =>
          resource.webResource
        } ~
        path(frontend) {
          frontendJs
        } ~
        path(blog ~ Slash.? ~ Remaining.?) { _ =>
          blogPage
        } ~
        path(postByTitle / Remaining) { title =>
          complete(`post repository`.query(title.withoutHttpSpaces))
        } ~
        path(posts / count) {
          complete(`post repository`.count.map(_.toJson))
        } ~
        path(posts / info) {
          complete(`info repository`.query)
        }
    } ~
      post {
        path(postsByTags) {
          entity(as[String]) { tags =>
            complete(`info repository`.query(tags.comaSeparatedList))
          }
        }
      }

  implicit class URLOps(value: String) {

    def withoutHttpSpaces: String = URLDecoder.decode(value, "UTF-8")

    def comaSeparatedList: List[String] = value.split(",").map(_.trim).toList
  }

}

object RestService {

  def routes(`post repository`: => PostRepository,
             `info repository`: => PostInfoRepository): Route =
    new RestService(`post repository`, `info repository`).routes
}