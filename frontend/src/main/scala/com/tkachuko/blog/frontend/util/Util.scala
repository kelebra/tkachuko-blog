package com.tkachuko.blog.frontend.util

import com.tkachuko.blog.models.Post
import org.scalajs.dom.MouseEvent
import upickle.default._

import scala.scalajs.js

object Util {

  implicit class JsonConversions(json: String) {

    def posts: List[Post] = read[List[Post]](json)

    def post: Post = read[Post](json)
  }

  implicit class TagsExtractor(posts: List[Post]) {

    def tags: Set[String] = posts.flatMap(_.tags).toSet
  }

  def highlightCode() = js.eval("Prism.highlightAll();")

  def onSideBarToggle(): MouseEvent => Unit = event => {
    js.eval("$('#sidebar').sidebar('toggle');")
    js.eval("$('.ui.dropdown').dropdown();")
  }
}
