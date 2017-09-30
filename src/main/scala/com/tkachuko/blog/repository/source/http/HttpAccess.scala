package com.tkachuko.blog.repository.source.http

import org.scalajs.dom.ext.Ajax

import scala.concurrent.{ExecutionContext, Future}

sealed trait HttpAccess {

  def send(request: HttpRequest)(implicit ec: ExecutionContext): Future[String]
}

object Native extends HttpAccess {

  def send(request: HttpRequest)(implicit ec: ExecutionContext): Future[String] =
    Ajax.get(request.build).map(_.responseText)
}

object HttpAccess {

  def native(implicit ex: ExecutionContext): HttpAccess = Native

  def success(json: => String)(implicit ec: ExecutionContext): HttpAccess =
    custom(_ => Future.successful(json))

  def failure(message: String)(implicit ec: ExecutionContext): HttpAccess =
    custom(_ => Future.failed(new RuntimeException(message)))

  def custom(json: HttpRequest => Future[String])(implicit ec: ExecutionContext): HttpAccess =
    new HttpAccess {
      def send(request: HttpRequest)(implicit ec: ExecutionContext): Future[String] = json(request)
    }
}
