package com.tkachuko.blog.db.namespace

import com.tkachuko.blog.client.{Namespace, Reply, Request}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Mixin that filters out any incoming messages that are not of type Request
  * or do not belong to the Namespace
  * In case of successful dispatch, request can be processed according to the Namespace
  */
trait NamespaceResolution extends PartialFunction[Any, Future[Reply[_]]] {

  val namespace: Namespace

  def isDefinedAt(x: Any): Boolean = x match {
    case request: Request => request.namespace == namespace
    case _ => false
  }

  def apply(value: Any): Future[Reply[_]] = process(value.asInstanceOf[Request])

  def process(request: Request): Future[Reply[_]]

  implicit class FutureReply[T](future: Future[T]) {

    def reply(request: Request) = future map {
      case data: T => request.success(data)
    } recover {
      case error: Throwable => request.failure(error.getMessage)
    }
  }

}
