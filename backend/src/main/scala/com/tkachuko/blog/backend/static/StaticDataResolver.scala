package com.tkachuko.blog.backend.static

import akka.http.scaladsl.server.Directives._

object StaticDataResolver {

  val resourcePrefix = "pages"

  val homePage = "index.html".asWebResource

  implicit class WebResource(path: String) {

    def asWebResource = getFromResource(s"$resourcePrefix/$path")
  }

}
