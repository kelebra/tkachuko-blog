package com.tkachuko.blog.repository

import akka.actor.{ActorSystem, Props}
import com.tkachuko.blog.common.ActorSpec
import com.tkachuko.blog.repository.source.memory.Read

class ExperienceActorTest extends ActorSpec(ActorSystem("experience-actor")) {

  "Experience actor" should {

    "provide experience per programming language" in {
      system.actorOf(Props(ExperienceActor)) ! Read
      val mapping = expectMsgClass(classOf[List[Experience]])
      mapping.find(_.language == "Scala(.js)") should not be empty
    }
  }
}
