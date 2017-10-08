package com.tkachuko.blog.repository.source.http

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import com.tkachuko.blog.common.ActorSpec

class EntityAccessActorTest extends ActorSpec(ActorSystem("entity-access-actor")) {

  import system.dispatcher

  "Entity access actor" should {

    "send valid http request for read post request" in {
      val collection = "col"
      val target = "post-title"
      val probe = TestProbe()
      stub(probe, collection) ! PostAccessActor.ReadPost(target)

      val request = probe.expectMsgClass(classOf[HttpRequest])
      request.path should endWith(s"""/collections/$collection?q={"title":"$target"}""")
    }

    "send valid http request for read post infos request" in {
      val collection = "col"
      val probe = TestProbe()
      stub(probe, collection) ! PostInfoActor.ReadPostInfos

      val request = probe.expectMsgClass(classOf[HttpRequest])
      request.path should endWith(s"""collections/$collection?f={"content":0}""")
    }
  }

  def stub(probe: TestProbe, collection: String): ActorRef =
    system.actorOf(Props(
      new EntityAccessActor(Option(HttpAccess.success("")), collection) {
        override protected def child: ActorRef = probe.ref
      }
    ))
}
