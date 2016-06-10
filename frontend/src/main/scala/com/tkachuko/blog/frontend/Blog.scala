package com.tkachuko.blog.frontend

import com.tkachuko.blog.frontend.controllers.Posts
import com.tkachuko.blog.frontend.views.BlogView
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object Blog extends js.JSApp {

  @JSExport
  def main(): Unit = {
    document.body.innerHTML = ""
    Posts.loadAll(BlogView.apply(_).renderIn(document.body))
  }
}
