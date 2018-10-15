package com.tkachuko.blog.repository

import com.tkachuko.blog.http.Endpoint
import com.tkachuko.blog.json.JsonRepository
import com.tkachuko.blog.models.PostInfo

import scala.concurrent.{ExecutionContext, Future}

trait PostInfoRepository {

  def load(implicit ec: ExecutionContext): Future[Seq[PostInfo]]

  def loadByTag(tag: String)(implicit ec: ExecutionContext): Future[Seq[PostInfo]]
}

case class HttpPostInfoRepository(endpoint: Endpoint, json: JsonRepository[PostInfo]) extends PostInfoRepository {

  override def load(implicit ec: ExecutionContext): Future[Seq[PostInfo]] = {
    val url = s"""https://api.mlab.com/api/1/databases/blog/collections/posts?f={"_id":0,"content":0}&apiKey=${endpoint.token}"""
    endpoint.get[Seq[PostInfo]](url)(json.multiple).map(_.sortBy(-_.created))
  }

  override def loadByTag(tag: String)(implicit ec: ExecutionContext): Future[Seq[PostInfo]] =
    load.map(_.filter(_.tags.contains(tag)))
}