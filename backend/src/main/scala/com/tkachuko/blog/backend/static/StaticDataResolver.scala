package com.tkachuko.blog.backend.static

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object StaticDataResolver {

  val resourcePrefix = "pages"

  val blog = "blog"

  val posts = "posts"

  val postByTitle = "post"

  val count = "count"

  val postsByTags = "tags"

  val info = "info"

  val frontend = "frontend-fastopt.js"

  val frontendJs: Route = frontend.plainResource

  val homePage: Route = "index.html".webResource

  val blogPage: Route = "blog.html".webResource

  implicit class WebResource(val path: String) extends AnyVal {

    def webResource: Route = getFromResource(s"$resourcePrefix/$path")

    def plainResource: Route = getFromResource(path)
  }

}
