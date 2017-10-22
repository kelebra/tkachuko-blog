package com.tkachuko.blog.repository.source.http

import org.scalatest.{Matchers, WordSpec}

class JsonSupportTest extends WordSpec with Matchers {

  "Json support" should {

    "decode posts array" in {
      JsonSupport.infoUnmarshal(
        """[ { "title" : "Back to school: reverse bits" , "tags" : [ "algorithms"] , "created" : 1.4682132E12} ,
          |{ "created" : 1.482972233298E12 , "title" : "Back to school: in order traversal of tree" , "tags" : [ "algorithms"]} ]
          |""".stripMargin
      ).size shouldBe 2
    }

    "decode info array" in {

    }
  }
}
