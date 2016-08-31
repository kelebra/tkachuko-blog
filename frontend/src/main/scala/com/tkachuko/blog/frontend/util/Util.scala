package com.tkachuko.blog.frontend.util

import com.tkachuko.blog.models.Post
import org.scalajs.dom.{Element, MouseEvent, Node, document}
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

  implicit class HTMLExtractor(value: String) {

    def byId: Element = document.getElementById(value)
  }

  def replaceBodyWith(elements: Node*): Unit = {
    document.body.innerHTML = ""
    elements.foreach(document.body.appendChild)
  }

  def replaceElementContent(element: Element, html: String, additional: Node*): Unit = {
    element.innerHTML = html
    additional.foreach(element.appendChild)
  }

  def replaceElementContent(element: Element, additional: Node*): Unit =
    replaceElementContent(element, "", additional: _*)

  def highlightCode() = js.eval("Prism.highlightAll();")

  def onSideBarToggle(): MouseEvent => Unit = event => {
    js.eval("$('#sidebar').sidebar('toggle');")
    js.eval("$('.ui.dropdown').dropdown();")
  }
}
