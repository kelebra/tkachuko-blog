package com.tkachuko.blog.http

import org.scalajs.dom.ext.Ajax

import scala.concurrent.{ExecutionContext, Future}

trait Endpoint {

  type URL = String
  type Json = String

  final val token = "lszijr65VV8oxrkkwPIolGFG0zpgpZhW"

  def get[T](url: URL)(conversion: Json ⇒ T)(implicit ec: ExecutionContext): Future[T]
}

object HttpEndpoint extends Endpoint {

  override def get[T](url: URL)(conversion: Json ⇒ T)(implicit ec: ExecutionContext): Future[T] =
    Ajax.get(url = url).map(response => conversion(response.responseText))
}
