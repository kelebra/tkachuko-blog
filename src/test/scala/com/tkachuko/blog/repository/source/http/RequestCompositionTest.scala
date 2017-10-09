package com.tkachuko.blog.repository.source.http

import com.tkachuko.blog.repository.source.http.PostAccessActor.ReadPost
import com.tkachuko.blog.repository.source.http.PostInfoActor.ReadPostInfos
import org.scalatest.{Matchers, WordSpec}

class RequestCompositionTest extends WordSpec with Matchers {

  "Request composition" should {

    "create http request for read post by title" in {
      val query = ReadPost("This is some post title")
      val collection = "some-collection"

      val httpRequest = EntityAccessActor.request(collection, query)
      httpRequest.path should endWith(s"""$collection?q={"title":"${query.title}"}""")
    }

    "create http request for read of all post infos" in {
      val collection = "some-collection"

      val httpRequest = EntityAccessActor.request(collection, ReadPostInfos)
      httpRequest.path should endWith(s"""$collection?f={"content":0}""")
    }
  }
}
