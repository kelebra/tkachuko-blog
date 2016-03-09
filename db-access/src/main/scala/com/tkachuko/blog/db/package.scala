package com.tkachuko.blog

import com.tkachuko.blog.models.Post
import com.typesafe.config.ConfigFactory
import scalikejdbc.{AutoSession, WrappedResultSet, _}
import skinny.DBSettings
import skinny.orm.{Alias, SkinnyCRUDMapper}

package object db {

  val config = ConfigFactory.load("application.conf")
  val init = config.getBoolean("development.init")

  object Database {

    implicit val session = AutoSession

    def initialize(): Unit = {
      DBSettings.initialize()
      if (init) {
        sql"drop table if exists POSTS;".execute().apply()
        sql"create table POSTS (id serial, title varchar(50), content varchar(100000));".execute().apply()
      }
    }

    object Posts extends SkinnyCRUDMapper[Post] {

      override def defaultAlias: Alias[Post] = createAlias("p")

      override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Post]): Post =
        Post(rs.long(n.id), rs.string(n.title), rs.string(n.content))

      override def tableName: String = "POSTS"
    }

    def save(post: Post) =
      sql"insert into POSTS (id, title, content) values(${post.id}, ${post.title}, ${post.content});".execute().apply()
  }

}
