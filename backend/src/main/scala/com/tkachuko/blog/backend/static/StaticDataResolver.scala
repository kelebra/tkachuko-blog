package com.tkachuko.blog.backend.static

import akka.http.scaladsl.server.Directives._

object StaticDataResolver {

  val resourcePrefix = "pages"

  val blog = "blog"

  val posts = "posts"

  val homePage = "index.html".asWebResource

  val blogPage = "blog.html".asWebResource

  implicit class WebResource(path: String) {

    def asWebResource = getFromResource(s"$resourcePrefix/$path")
  }

}
