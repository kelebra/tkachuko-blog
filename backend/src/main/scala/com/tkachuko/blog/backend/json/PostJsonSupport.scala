package com.tkachuko.blog.backend.json

import com.tkachuko.blog.models.Post
import spray.json._

object PostJsonSupport extends DefaultJsonProtocol {

  implicit val jsonFormat = jsonFormat3(Post)
}
