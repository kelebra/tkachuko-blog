package com.tkachuko.blog

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

package object util {

  implicit class FutureWithEither[T](future: Future[T]) {

    def <\/>(implicit ec: ExecutionContext): Future[\/[Throwable, T]] =
      future
        .map(\/-.apply)
        .recover { case failure => -\/(failure) }
  }

}
