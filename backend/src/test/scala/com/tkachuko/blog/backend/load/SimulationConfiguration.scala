package com.tkachuko.blog.backend.load

import com.typesafe.config.ConfigFactory

import scala.util.Try

trait SimulationConfiguration {

  val host: String = s"http://${config.getString("host")}"

  val repeats: Int = Try(config.getInt("repeats")).getOrElse(1)

  val users: Int = Try(config.getInt("users")).getOrElse(10)

  private lazy val config = ConfigFactory.load("simulation.conf")
}
