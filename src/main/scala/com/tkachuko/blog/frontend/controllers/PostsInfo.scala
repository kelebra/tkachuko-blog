package com.tkachuko.blog.frontend.controllers

import com.tkachuko.blog.json.{JsonRepository, JsonService}
import com.tkachuko.blog.models.PostInfo
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PostsInfo extends AsyncLoader {

  this: JsonService[PostInfo] =>

  type Id = String

  type Data = PostInfo

  private def loadAllInfos: Future[List[PostInfo]] =
    Ajax.get(url = s"""https://api.mlab.com/api/1/databases/blog/collections/posts?f={"_id":0,"content":0}&apiKey=$token""")
      .map(r => json.fromJson(r.responseText))

  def loadAll(callback: Callback): Unit =
    loadAllInfos.map(_.sortBy(-_.created)).foreach(callback)

  def loadWithTag(tag: String)(callback: Callback): Unit =
    loadAllInfos.map(_.sortBy(-_.created))
      .map(_.filter(_.tags.contains(tag)))
      .foreach(callback)

  def loadOne(id: Id)(callback: Callback): Unit = ???

}

object PostsInfo extends PostsInfo with JsonService[PostInfo] {

  override val json = JsonRepository.postInfo
}