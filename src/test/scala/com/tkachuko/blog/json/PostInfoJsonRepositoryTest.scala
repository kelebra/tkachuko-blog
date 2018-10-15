package com.tkachuko.blog.json

import com.tkachuko.blog.models.PostInfo
import utest.{Tests, _}

object PostInfoJsonRepositoryTest extends TestSuite {

  val tests = Tests {

    "deserialize post info" - {
      val json =
        """[ {
          |"title" : "Back to school: reverse bits" ,
          |"tags" : [ "algorithms"] ,
          |"created" : 1.4682132E12
          |}]""".stripMargin
      PostInfoJsonRepository.multiple(json) ==> List(
        PostInfo(
          title = "Back to school: reverse bits",
          tags = List("algorithms"),
          created = 1.4682132E12
        )
      )
    }
  }
}