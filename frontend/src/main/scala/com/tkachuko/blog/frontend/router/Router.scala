package com.tkachuko.blog.frontend.router

import com.tkachuko.blog.frontend.controllers.Posts
import com.tkachuko.blog.frontend.views.BlogView
import org.scalajs.dom._

object Router {

  def apply(url: String) = {
    url match {
      case post if url.containsPost =>
        post.title.foreach(title => Posts.loadOne(title)(BlogView.apply(_).render))
      case tag if url.containsTag =>
        tag.tag.foreach(name => Posts.loadWithTag(name)(BlogView.apply(_).render))
      case _ => Posts.loadAll(BlogView.apply(_).render)
    }
    url.render()
  }

  def setupHistoryListener() =
    window.addEventListener("popstate", (event: PopStateEvent) => Router(window.location.href))

  implicit class URLOps(value: String) {

    val jsUrlSuffix = "#"

    val postSuffix = "post="

    val tagSuffix = "tag="

    def containsPost = value.contains(s"$jsUrlSuffix$postSuffix")

    def containsTag = value.contains(s"$jsUrlSuffix$tagSuffix")

    def title = value.split(postSuffix).lastOption

    def tag = value.split(tagSuffix).lastOption

    def tagUrl = s"/blog$jsUrlSuffix$tagSuffix$value"

    def postUrl = s"/blog$jsUrlSuffix$postSuffix$value"

    def render(): Unit = {
      val currentValue = window.location.href
      if (currentValue != value) window.location.href = value
    }

    def sanitize = value.filter(_.isLetterOrDigit)
  }

}
