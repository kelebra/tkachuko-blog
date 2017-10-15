package com.tkachuko.blog.repository

import akka.actor.{ActorSystem, Props}
import com.tkachuko.blog.common.ActorSpec
import com.tkachuko.blog.repository.source.memory.Read

class GithubActorTest extends ActorSpec(ActorSystem("github-actor")) {

  "Github actor" should {

    "provide list of github repositories" in {
      system.actorOf(Props(GithubActor)) ! Read
      val links = expectMsgClass(classOf[List[String]])
      links.find(_.contains("tkachuko-blog")) should not be empty
    }
  }
}
