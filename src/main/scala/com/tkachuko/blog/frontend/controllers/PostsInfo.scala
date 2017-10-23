package com.tkachuko.blog.frontend.controllers

import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.PostInfo
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PostsInfo extends AsyncLoader {

  type Id = String

  type Data = PostInfo

  private def loadAllInfos: Future[List[PostInfo]] =
    Ajax.get(url = s"""https://api.mlab.com/api/1/databases/blog/collections/posts?f={"_id":0,"content":0}&apiKey=$token""")
      .map(_.responseText.postsInfo)

  def loadAll(callback: Callback): Unit =
    loadAllInfos.map(_.sortBy(-_.created)).foreach(callback)

  def loadWithTag(tag: String)(callback: Callback): Unit =
    loadAllInfos.map(_.sortBy(-_.created))
      .map(_.filter(_.tags.contains(tag)))
      .foreach(callback)

  def loadOne(id: Id)(callback: Callback): Unit = ???

}
