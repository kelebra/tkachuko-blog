package com.tkachuko.blog.db

import com.tkachuko.blog.models.Post
import org.h2.tools.Server
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class DatabaseSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  "Database" should {

    "be able to persist and retrieve persisted record" in {
      val post = Post(content = "content")
      Database.save(post)
      Database.query[Post].fetch() should not be empty
    }
  }

  override protected def beforeAll(): Unit = Server.createTcpServer("-tcpAllowOthers").start()
}