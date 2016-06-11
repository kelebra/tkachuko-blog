package com.tkachuko.blog.frontend.util

import com.tkachuko.blog.models.Post
import upickle.default._

import scala.scalajs.js

object Util {

  implicit class JsonConversions(json: String) {

    def posts: List[Post] = read[List[Post]](json)

    def post: Post = read[Post](json)
  }

  def highlightCode() = js.eval("Prism.highlightAll();")
}
