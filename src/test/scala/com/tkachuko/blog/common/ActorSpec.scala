package com.tkachuko.blog.common

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

abstract class ActorSpec(_system: => ActorSystem) extends TestKit(_system)
  with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender with DefaultTimeout {

  final override def afterAll: Unit = shutdown()
}
