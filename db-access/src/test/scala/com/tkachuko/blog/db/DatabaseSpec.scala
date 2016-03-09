package com.tkachuko.blog.db

import com.tkachuko.blog.models.Post
import org.h2.tools.Server
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class DatabaseSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  val id = System.currentTimeMillis()

  "Database" should {

    "retrieve persisted record by id" in {
      Database.Posts.findById(id) should be('defined)
    }
  }

  override protected def beforeAll(): Unit = {
    Server.createTcpServer("-tcpAllowOthers").start()
    Database.initialize()
    Database.save(Post(id = id, title = "title", content = "content"))
  }
}