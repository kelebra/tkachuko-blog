package com.tkachuko.blog.repository.source.http

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import com.tkachuko.blog.common.ActorSpec
import com.tkachuko.blog.model.{Infos, Post, PostInfo, Posts}
import com.tkachuko.blog.repository.source.http.JsonSupport._
import com.tkachuko.blog.repository.source.http.PostAccessActor.ReadPost
import com.tkachuko.blog.repository.source.http.PostInfoActor.ReadPostInfos

import scalaz.\/-

class EntityAccessActorTest extends ActorSpec(ActorSystem("entity-access-actor")) {

  import system.dispatcher

  "Entity access actor" should {

    "send valid http request for read post request" in {
      val collection = "col"
      val target = "post-title"
      val probe = TestProbe()
      stub[Posts](probe, collection) ! ReadPost(target)

      val request = probe.expectMsgClass(classOf[HttpRequest])
      request.path should endWith(s"""/collections/$collection?q={"title":"$target"}""")
    }

    "send valid http request for read post infos request" in {
      val collection = "col"
      val probe = TestProbe()
      stub[Infos](probe, collection) ! ReadPostInfos

      val request = probe.expectMsgClass(classOf[HttpRequest])
      request.path should endWith(s"""collections/$collection?f={"_id":0,"content":0}""")
    }

    "receive deserialized list of infos" in {
      val success = HttpAccess.success(
        """
          |[ { "title" : "Back to school: reverse bits" , "tags" : [ "algorithms"] , "created" : 1.4682132E12} ,
          |{ "created" : 1.482972233298E12 , "title" : "Back to school: in order traversal of tree" , "tags" : [ "algorithms"]} ]
        """.stripMargin
      )
      system.actorOf(Props(new EntityAccessActor[Infos]("bla", () => success) {})) ! ReadPostInfos

      val reply = expectMsgClass(classOf[\/-[Infos]])
      reply.getOrElse(Set.empty) should not be empty
    }
  }

  private def stub[T](probe: TestProbe, collection: String)(implicit un: Unmarshal[T]) =
    system.actorOf(Props(
      new EntityAccessActor[T](collection, () => HttpAccess.success("")) {

        override protected def child: ActorRef = probe.ref
      }
    ))
}
