package com.tkachuko.blog.frontend.router

import com.tkachuko.blog.frontend.router.Router.URLOps
import utest._

object URLOpsSpec extends TestSuite {

  val tests = Tests {

    "contain post reference" - {
      val url = "http://domain:port/resource#/blog/post=hello%20world"
      url.containsPost ==> true
    }

    "not contain post reference" - {
      val url = "http://domain:port/blog"
      url.containsPost ==> false
    }

    "be used to extract post title" - {
      val url = "http://domain:port/resource#/blog/post=hello%20world"
      url.title ==> Option("hello%20world")
    }

    "be used to extract tag" - {
      val url = "http://domain:port/resource#/blog/tag=scala"
      url.tag ==> Option("scala")
    }

    "contain tag query" - {
      val url = "http://domain:port/resource#/blog/tag=scala"
      url.containsTag ==> true
    }
  }
}
