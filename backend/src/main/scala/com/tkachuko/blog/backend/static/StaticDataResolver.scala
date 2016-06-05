package com.tkachuko.blog.backend.static

import akka.http.scaladsl.server.Directives._

object StaticDataResolver {

  val resourcePrefix = "pages"

  val blog = "blog"

  val posts = "posts"

  val postByTitle = "post"

  val postsByTags = "tags"

  val subscribe = "subscribe"

  val homePage = "index.html".asWebResource

  val blogPage = "blog.html".asWebResource

  implicit class WebResource(val path: String) extends AnyVal {

    def asWebResource = getFromResource(s"$resourcePrefix/$path")
  }

}
