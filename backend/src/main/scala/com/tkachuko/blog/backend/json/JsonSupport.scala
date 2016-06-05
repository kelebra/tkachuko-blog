package com.tkachuko.blog.backend.json

import com.tkachuko.blog.models.Post
import spray.json._

object JsonSupport extends DefaultJsonProtocol {

  implicit val postJsonFormat = jsonFormat4(Post)
}
