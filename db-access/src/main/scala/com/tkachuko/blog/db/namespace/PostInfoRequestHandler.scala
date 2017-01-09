package com.tkachuko.blog.db.namespace

import com.tkachuko.blog.client._
import com.tkachuko.blog.db.repository.PostInfoRepository

import scala.concurrent.Future

case class PostInfoRequestHandler(repository: PostInfoRepository) extends NamespaceResolution {

  val namespace: Namespace = PostsDescriptions

  def process(request: Request): Future[Reply[_]] = (
    request match {
      case all: AllInfo => repository.query
      case _ => Future.successful(
        request.failure(s"Operation ${request.getClass.getSimpleName} is not supported")
      )
    }).reply(request)
}
