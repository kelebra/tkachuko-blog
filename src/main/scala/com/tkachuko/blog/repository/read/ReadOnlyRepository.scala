package com.tkachuko.blog.repository.read

import scala.concurrent.Future

trait ReadOnlyRepository[S, P, I] {

  def read(id: I): Future[Option[S]]

  def all: P
}
