package com.tkachuko.blog.repository.source.memory

import akka.actor.ActorSystem
import com.tkachuko.blog.common.ActorSpec

class InMemorySourceTest extends ActorSpec(ActorSystem("in-memory-actor-spec")) {

  "In-memory data actor" should {

    "return stored data ob read request" in {

      val hello = "hello"
      system.actorOf(InMemorySource(hello)) ! Read

      expectMsg(hello)
    }
  }
}
