package com.tkachuko.blog.db.namespace

import com.tkachuko.blog.client._
import com.tkachuko.blog.db.repository.PostRepository

import scala.concurrent.Future

case class PostsRequestHandler(repository: PostRepository) extends NamespaceResolution {

  val namespace: Namespace = Posts

  def process(request: Request): Future[Reply[_]] = (
    request match {
      case all: All => repository.query
      case search: FindByTitle => repository.query(search.title)
      case search: FindByTags => repository.query(search.tags)
      case count: Count => repository.count
      case insert: Insert => repository.insert(insert.post)
      case _ => Future.successful(
        request.failure(s"Operation ${request.getClass.getSimpleName} is not supported")
      )
    }).reply(request)
}
