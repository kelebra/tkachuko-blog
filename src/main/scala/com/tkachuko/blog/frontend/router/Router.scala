package com.tkachuko.blog.frontend.router

import com.tkachuko.blog.frontend.views.{BlogView, Index}
import com.tkachuko.blog.http.HttpEndpoint
import com.tkachuko.blog.json.{PostInfoJsonRepository, PostJsonRepository}
import com.tkachuko.blog.repository.{HttpPostInfoRepository, HttpPostRepository, PostInfoRepository, PostRepository}
import org.scalajs.dom._

import scala.concurrent.ExecutionContext.Implicits.global

object Router {

  val postRepository: PostRepository =
    HttpPostRepository(HttpEndpoint, PostJsonRepository)
  val postInfoRepository: PostInfoRepository =
    HttpPostInfoRepository(HttpEndpoint, PostInfoJsonRepository)

  val jsUrlSuffix = "#"

  val blogSuffix = s"$jsUrlSuffix/blog/"

  val commentsSuffix = s"${jsUrlSuffix}comment"

  val postSuffix = s"${blogSuffix}post="

  val tagSuffix = s"${blogSuffix}tag="

  def apply(url: String): Unit = {
    url match {
      case ""                       ⇒ Index.render()
      case post if url.containsPost ⇒
        post.title.foreach(title =>
          postRepository.load(title).map(_.toList).foreach(BlogView.renderPosts)
        )
      case tag if url.containsTag   ⇒
        tag.tag.foreach(name =>
          postInfoRepository.loadByTag(name).map(_.toList).foreach(BlogView.renderPostsInfo))
      case _ if url.containsComment ⇒ Unit
      case _                        =>
        postInfoRepository.load.map(_.toList).foreach(BlogView.renderPostsInfo)
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

    def containsComment: Boolean = value contains commentsSuffix

    def title: Option[String] = value.split(postSuffix).lastOption

    def tag: Option[String] = value.split(tagSuffix).lastOption

    def renderAddressBar(): Unit = {
      val currentValue = window.location.hash
      if (currentValue != value) window.location.hash = value
    }

    def sanitize: String = value.filter(_.isLetterOrDigit)
  }

}
