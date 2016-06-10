package com.tkachuko.blog.frontend.controllers

import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.Post
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global

object Posts {

  def loadAll(callback: List[Post] => Unit): Unit =
    Ajax.get(url = "/posts").onSuccess { case xhr => callback(xhr.responseText.posts) }
}
