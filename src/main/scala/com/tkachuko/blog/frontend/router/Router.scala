package com.tkachuko.blog.frontend.router

import com.tkachuko.blog.frontend.controllers.{Posts, PostsInfo}
import com.tkachuko.blog.frontend.views.{BlogView, Index}
import org.scalajs.dom._

object Router {

  val jsUrlSuffix = "#"
  val blogSuffix = s"$jsUrlSuffix/blog/"

  val postSuffix = s"${blogSuffix}post="

  val tagSuffix = s"${blogSuffix}tag="

  def apply(url: String): Unit = {
    url match {
      case ""                       => Index.render()
      case post if url.containsPost =>
        post.title.foreach(title => Posts.loadOne(title)(BlogView.renderPosts))
      case tag if url.containsTag   =>
        tag.tag.foreach(name => PostsInfo.loadWithTag(name)(BlogView.renderPostsInfo))
      case _                        => PostsInfo.loadAll(BlogView.renderPostsInfo)
    }
    url.renderAddressBar()
  }

  def setupHistoryListener(): Unit =
    window.addEventListener("popstate", (event: PopStateEvent) => Router(window.location.hash))

  private def goTo(hash: String): MouseEvent => Unit = _ => window.location.hash = hash

  def goToBlog: MouseEvent => Unit = goTo(blogSuffix)

  def goToTitle(t: String): MouseEvent => Unit = goTo(postSuffix concat t)

  def goToTag(t: String): MouseEvent => Unit = goTo(tagSuffix concat t)

  def goHome: MouseEvent => Unit = goTo("")

  implicit class URLOps(value: String) {

    def containsPost: Boolean = value contains postSuffix

    def containsTag: Boolean = value contains tagSuffix

    def title: Option[String] = value.split(postSuffix).lastOption

    def tag: Option[String] = value.split(tagSuffix).lastOption

    def renderAddressBar(): Unit = {
      val currentValue = window.location.hash
      if (currentValue != value) window.location.hash = value
    }

    def sanitize: String = value.filter(_.isLetterOrDigit)
  }

}
