package com.tkachuko.blog.backend.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tkachuko.blog.models.Post
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val postJsonFormat = jsonFormat4(Post)
}
