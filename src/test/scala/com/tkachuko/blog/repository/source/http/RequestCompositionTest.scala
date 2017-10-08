package com.tkachuko.blog.repository.source.http

import com.tkachuko.blog.repository.source.http.EntityAccessActor.Query
import org.scalatest.{Matchers, WordSpec}

class RequestCompositionTest extends WordSpec with Matchers {

  "Request composition" should {

    "create http request for present filter parameter" in {
      new Query {
        override val filter = None
      }
    }
  }
}
