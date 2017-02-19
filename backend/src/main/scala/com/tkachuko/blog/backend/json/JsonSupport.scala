package com.tkachuko.blog.backend.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tkachuko.blog.models.{Post, PostInfo}
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val postJsonFormat: RootJsonFormat[Post] = jsonFormat4(Post)

  implicit val postInfoJsonFormat: RootJsonFormat[PostInfo] = jsonFormat3(PostInfo)
}
