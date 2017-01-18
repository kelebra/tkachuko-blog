package com.tkachuko.blog.frontend.router

import com.tkachuko.blog.frontend.controllers.{Posts, PostsInfo}
import com.tkachuko.blog.frontend.views.BlogView
import org.scalajs.dom._

object Router {

  def apply(url: String) = {
    url match {
      case post if url.containsPost =>
        post.title.foreach(title => Posts.loadOne(title)(BlogView.renderPosts))
      case tag if url.containsTag =>
        tag.tag.foreach(name => PostsInfo.loadWithTag(name)(BlogView.renderPostsInfo))
      case _ => PostsInfo.loadAll(BlogView.renderPostsInfo)
    }
    url.renderAddressBar()
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

    def renderAddressBar(): Unit = {
      val currentValue = window.location.href
      if (currentValue != value) window.location.href = value
    }

    def sanitize = value.filter(_.isLetterOrDigit)
  }

}
