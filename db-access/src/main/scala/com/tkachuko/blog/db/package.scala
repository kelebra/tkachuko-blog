package com.tkachuko.blog

import com.tkachuko.blog.models.Post
import com.typesafe.config.ConfigFactory
import sorm.{Entity, InitMode, Instance}

package object db {

  val config = ConfigFactory.load("db.conf")

  object Database extends Instance(
    entities = Set(Entity[Post]()),
    url = config.getString("db.url"),
    user = config.getString("db.user"),
    password = config.getString("db.password"),
    initMode = if (config.getBoolean("db.init")) InitMode.DropAllCreate else InitMode.DoNothing
  ) {}
}
