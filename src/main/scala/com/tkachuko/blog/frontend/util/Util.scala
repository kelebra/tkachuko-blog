package com.tkachuko.blog.frontend.util

import java.util.concurrent.TimeUnit

import com.tkachuko.blog.models.{Post, PostInfo}
import org.scalajs.dom.{Element, MouseEvent, Node, document}

import scala.scalajs.js

object Util {

  implicit class PostTagsExtractor(posts: List[Post]) {

    def tags: Set[String] = posts.flatMap(_.tags).toSet
  }

  implicit class PostInfoTagsExtractor(posts: List[PostInfo]) {

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
          "year" -> duration / TimeUnit.DAYS.toMillis(365),
          "month" -> duration / TimeUnit.DAYS.toMillis(31),
          "day" -> duration / TimeUnit.DAYS.toMillis(1),
          "hour" -> duration / TimeUnit.HOURS.toMillis(1),
          "minute" -> duration / TimeUnit.MINUTES.toMillis(1),
          "second" -> duration / TimeUnit.SECONDS.toMillis(1)
        ).mapValues(math.abs)
          .filter { case (_, elapsed) => elapsed > 0 }
          .minBy { case (_, elapsed) => elapsed }
      s"$value ${if (value > 1) unit + "s" else unit}"
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

  def highlightCode(): Unit = js.eval("Prism.highlightAll();")

  def renderGraphics(): Unit = js.eval("mermaid.init();")

  def onSideBarToggle(): MouseEvent => Unit = event => {
    js.eval("$('#sidebar').sidebar('toggle');")
    js.eval("$('.ui.dropdown').dropdown();")
  }
}
