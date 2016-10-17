package com.tkachuko.blog.db.namespace

import com.tkachuko.blog.client._
import com.tkachuko.blog.db.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PostsRequestHandler extends NamespaceResolution {

  val namespace: Namespace = Posts

  def process(request: Request): Future[Reply[_]] = (
    request match {
      case all: All => Database.Posts.all()
      case search: FindByTitle => Database.Posts.findByTitle(search.title)
      case search: FindByTags => Database.Posts.findByTags(search.tags)
      case count: Count => Database.Posts.count()
      case insert: Insert => Database.Posts.insert(insert.post)
      case _ => Future.successful(
        request.failure(s"Operation ${request.getClass.getSimpleName} is not supported")
      )
    }).reply(request)

  implicit class FutureReply[T](future: Future[T]) {

    def reply(request: Request) = future map {
      case data: T => request.success(data)
    } recover {
      case error: Throwable => request.failure(error.getMessage)
    }
  }

}
