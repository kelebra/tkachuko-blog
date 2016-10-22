package com.tkachuko.blog.backend.static

import akka.http.scaladsl.server.Directives._

object StaticDataResolver {

  val resourcePrefix = "pages"

  val blog = "blog"

  val posts = "posts"

  val postByTitle = "post"

  val count = "count"

  val postsByTags = "tags"

  val frontend = "frontend-fastopt.js"

  val frontendJs = frontend.asPlainResource

  val homePage = "index.html".asWebResource

  val blogPage = "blog.html".asWebResource

  implicit class WebResource(val path: String) extends AnyVal {

    def asWebResource = getFromResource(s"$resourcePrefix/$path")

    def asPlainResource = getFromResource(path)
  }

}
