package com.tkachuko.blog.json

import com.tkachuko.blog.models.Post
import utest.{Tests, _}

object PostJsonRepositoryTest {

  val tests = Tests {

    "deserialize post" - {
      val json =
        """
          |[{
          |"_id" : { "$oid" : "5a1f8ae0734d1d3ed2314713"} ,
          |"created" : 1512016362168.0,
          |"content" : "hello!",
          |"title" : "Mastering scala: Day 4",
          |"tags" : [ "scala"]
          |}]
        """.stripMargin
      JsonRepository.posts.fromJson(json) ==> List(
        Post(
          title = "Mastering scala: Day 4",
          content = "hello!",
          tags = List("scala"),
          created = 1512016362168L
        )
      )
    }
  }
}
