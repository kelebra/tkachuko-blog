package com.tkachuko.blog

import java.util.concurrent.TimeUnit

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

package object util {

  implicit class FutureInterOps[T](future: Future[T]) {

    def <\/>(implicit ec: ExecutionContext): Future[\/[Throwable, T]] =
      future
        .map(\/-.apply)
        .recover { case failure => -\/(failure) }
  }

  implicit class ReadableMillisDifference(millis: Long) {

    private val units = Map(
      "year" -> TimeUnit.DAYS.toMillis(365),
      "month" -> TimeUnit.DAYS.toMillis(31),
      "day" -> TimeUnit.DAYS.toMillis(1),
      "hour" -> TimeUnit.HOURS.toMillis(1),
      "minute" -> TimeUnit.MINUTES.toMillis(1),
      "second" -> TimeUnit.SECONDS.toMillis(1)
    )

    def readableFromNow(now: Long = System.currentTimeMillis()): String = {
      val duration = System.currentTimeMillis() - millis.toLong
      val (unit, value) =
        units
          .mapValues(duration / _)
          .mapValues(math.abs)
          .filter { case (_, elapsed) => elapsed > 0 }
          .minBy { case (_, elapsed) => elapsed }
      s"$value ${if (value > 1) unit + "s" else unit}"
    }
  }
}
