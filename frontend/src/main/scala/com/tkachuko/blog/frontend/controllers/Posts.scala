package com.tkachuko.blog.frontend.controllers

import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.Post
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global

object Posts extends AsyncLoader {

  type Id = String

  type Data = Post

  def loadAll(callback: Callback): Unit = ???

  def loadOne(title: Id)(callback: Callback): Unit =
    Ajax.get(url = s"/post/$title").onSuccess { case xhr => callback(List(xhr.responseText.post)) }

  def count(callback: StringCallback): Unit =
    Ajax.get(url = "/posts/count").onSuccess { case xhr => callback(xhr.responseText) }
}
