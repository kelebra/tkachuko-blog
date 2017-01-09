package com.tkachuko.blog.frontend.controllers

import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.PostInfo
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global

object PostsInfo extends AsyncLoader {

  type Id = String

  type Data = PostInfo

  def loadAll(callback: Callback) =
    Ajax.get(url = "/posts/info").onSuccess { case xhr => callback(xhr.responseText.postsInfo) }

  def loadOne(id: Id)(callback: Callback): Unit = ???

  def count(callback: StringCallback) = ???

}
