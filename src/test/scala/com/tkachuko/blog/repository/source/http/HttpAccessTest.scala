package com.tkachuko.blog.repository.source.http

import org.scalatest.{AsyncWordSpec, Matchers}

import scala.concurrent.ExecutionContextExecutor

class HttpAccessTest extends AsyncWordSpec with Matchers {

  implicit override def executionContext: ExecutionContextExecutor =
    scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  "HTTP access" should {

    "make native http call and return reply" in {

      HttpAccess.native.send(HttpRequest(host = "ip.jsontest.com"))
        .map(reply => reply should not be empty)
    }
  }
}
