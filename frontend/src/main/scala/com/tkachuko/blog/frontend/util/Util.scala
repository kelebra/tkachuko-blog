package com.tkachuko.blog.frontend.util

import java.util.concurrent.TimeUnit

import com.tkachuko.blog.models.Post
import org.scalajs.dom.{Element, MouseEvent, Node, document}

import scala.scalajs.js

object Util {

  implicit class JsonConversions(json: String) {

    import upickle.default._

    def posts: List[Post] = read[List[Post]](json)

    def post: Post = read[Post](json)
  }

  implicit class TagsExtractor(posts: List[Post]) {

    def tags: Set[String] = posts.flatMap(_.tags).toSet
  }

  implicit class HTMLExtractor(value: String) {

    def byId: Element = document.getElementById(value)
  }

  implicit class MillisToReadableDifference(millis: Double) {

    def readableDifference: String = {
      val duration = System.currentTimeMillis() - millis.toLong
      val (unit, value) =
        Map(
          "year(s)" -> duration / TimeUnit.DAYS.toMillis(365),
          "month(s)" -> duration / TimeUnit.DAYS.toMillis(31),
          "day(s)" -> duration / TimeUnit.DAYS.toMillis(1),
          "hour(s)" -> duration / TimeUnit.HOURS.toMillis(1),
          "minute(s)" -> duration / TimeUnit.MINUTES.toMillis(1),
          "second(s)" -> duration / TimeUnit.SECONDS.toMillis(1)
        ).mapValues(math.abs)
          .filter { case (_, elapsed) => elapsed > 0 }
          .minBy { case (_, elapsed) => elapsed }
      s"$value $unit"
    }
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

  def renderGraphics() = js.eval("mermaid.initialize();")

  def onSideBarToggle(): MouseEvent => Unit = event => {
    js.eval("$('#sidebar').sidebar('toggle');")
    js.eval("$('.ui.dropdown').dropdown();")
  }
}
