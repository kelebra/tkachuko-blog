package com.tkachuko.blog.db

import com.typesafe.config.ConfigFactory
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network

object InMemoryMongo {

  val config = ConfigFactory.load("db.conf")

  lazy val mongodConfig = new MongodConfigBuilder()
    .version(Version.Main.V3_0)
    .net(new Net(config.getString("db.host"), config.getInt("db.port"), Network.localhostIsIPv6))
    .build

  lazy val mongod = MongodStarter.getDefaultInstance.prepare(mongodConfig)

  def start() = mongod.start()

  def stop() = mongod.stop()

}
