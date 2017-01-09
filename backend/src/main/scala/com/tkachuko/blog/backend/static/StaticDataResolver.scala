package com.tkachuko.blog.backend.static

import akka.http.scaladsl.server.Directives._

object StaticDataResolver {

  val resourcePrefix = "pages"

  val blog = "blog"

  val posts = "posts"

  val postByTitle = "post"

  val count = "count"

  val postsByTags = "tags"

  val info = "info"

  val frontend = "frontend-fastopt.js"

  val frontendJs = frontend.plainResource

  val homePage = "index.html".webResource

  val blogPage = "blog.html".webResource

  implicit class WebResource(val path: String) extends AnyVal {

    def webResource = getFromResource(s"$resourcePrefix/$path")

    def plainResource = getFromResource(path)
  }

}
