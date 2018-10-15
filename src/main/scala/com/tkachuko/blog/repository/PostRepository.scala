package com.tkachuko.blog.repository

import com.tkachuko.blog.http.Endpoint
import com.tkachuko.blog.json.JsonRepository
import com.tkachuko.blog.models.Post

import scala.concurrent.{ExecutionContext, Future}

trait PostRepository {

  def load(title: String)(implicit ec: ExecutionContext): Future[Option[Post]]
}

case class HttpPostRepository(endpoint: Endpoint, json: JsonRepository[Post]) extends PostRepository {

  override def load(title: String)(implicit ec: ExecutionContext): Future[Option[Post]] = {
    val url = s"""https://api.mlab.com/api/1/databases/blog/collections/posts?q={"title":"$title"}&apiKey=${endpoint.token}"""
    endpoint.get[Option[Post]](url)(json.single)
  }
}