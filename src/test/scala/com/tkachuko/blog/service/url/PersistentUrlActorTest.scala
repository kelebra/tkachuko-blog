package com.tkachuko.blog.service.url

import akka.actor.ActorSystem
import com.tkachuko.blog.common.ActorSpec

class PersistentUrlActorTest extends ActorSpec(ActorSystem("persistent-url-system")) {

  private val initial = "url"
  private val url = system.actorOf(PersistentUrlActor(initial))

  "Persistent url actor" should {

    "not change URL if it did not change" in {
      url ! initial
      expectNoMsg()
    }

    "change URL and send post as reply" in {
      val title = "Hello world"
      url ! s"http://domain.com/blog#post=$title"
      expectMsg(PostEvent(title))
    }

    "change URL and send tag as reply" in {
      val tag = "scala"
      url ! s"http://domain.com/blog#tag=$tag"
      expectMsg(TagEvent(tag))
    }

    "change URL and blog as reply" in {
      val tag = "scala"
      url ! s"http://domain.com/blog#bla=$tag"
      expectMsg(BlogEvent)
    }
  }
}
