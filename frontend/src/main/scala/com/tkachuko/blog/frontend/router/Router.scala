package com.tkachuko.blog.frontend.router

import com.tkachuko.blog.frontend.controllers.Posts
import com.tkachuko.blog.frontend.views.BlogView
import org.scalajs.dom._

object Router {

  def apply(url: String) = url match {
    case post if url.containsPost =>
      Posts.loadOne(post.postTitle, BlogView.apply(_).render)
    case tag if url.containsTag =>
      Posts.loadWithTag(tag.tag, BlogView.apply(_).render)
    case _ =>
      Posts.loadAll(BlogView.apply(_).render)
  }

  implicit class URLOps(value: String) {

    val jsUrlSuffix = "#"

    val postSuffix = "post="

    val tagSuffix = "tag="

    def containsPost = value.contains(s"$jsUrlSuffix$postSuffix")

    def containsTag = value.contains(s"$jsUrlSuffix$tagSuffix")

    def postTitle = value.split(postSuffix).last

    def tag = value.split(tagSuffix).last

    def toTagUrl = s"${window.location.host}/blog$jsUrlSuffix$tagSuffix$value"
  }

}
