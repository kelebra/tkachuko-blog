package com.tkachuko.blog.frontend.router

import com.tkachuko.blog.frontend.router.Router.URLOps
import org.scalatest.{Matchers, WordSpec}

class URLOpsSpec extends WordSpec with Matchers {

  "URL as string" should {

    "contain post reference" in {
      val url = "http://domain:port/resource#post=hello%20world"
      url.containsPost shouldBe true
    }

    "not contain post reference" in {
      val url = "http://domain:port/blog"
      url.containsPost shouldBe false
    }

    "can be used to extract post title" in {
      val url = "http://domain:port/resource#post=hello%20world"
      url.postTitle shouldBe "hello%20world"
    }
  }
}
