package com.tkachuko.blog.frontend.controllers

import com.tkachuko.blog.json.{JsonRepository, JsonService}
import com.tkachuko.blog.models.Post
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global

trait Posts extends AsyncLoader {
  this: JsonService[Post] =>

  type Id = String

  type Data = Post

  def loadAll(callback: Callback): Unit = ???

  def loadOne(title: Id)(callback: Callback): Unit =
    Ajax.get(url = s"""https://api.mlab.com/api/1/databases/blog/collections/posts?q={"title":"$title"}&apiKey=$token""")
      .map(r => json.fromJson(r.responseText))
      .foreach(callback)
}

object Posts extends Posts with JsonService[Post] {

  override val json = JsonRepository.posts
}