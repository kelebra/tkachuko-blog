package com.tkachuko.blog.repository.source.http

import akka.actor.ActorSystem
import com.tkachuko.blog.common.ActorSpec
import com.tkachuko.blog.model.Infos
import com.tkachuko.blog.repository.source.http.JsonSupport.infoUnmarshal

class HttpAccessActorTest extends ActorSpec(ActorSystem("HttpAccessActorTest")) {

  import system.dispatcher

  "Http actor" should {

    "make successful http call and deserialize json" in {
      val success = HttpAccess.success(
        """
          |[{ "title" : "Back to school: reverse bits" , "tags" : [ "algorithms"] , "created" : 1.4682132E12}]
        """.stripMargin
      )
      system.actorOf(HttpAccessActor(success)(infoUnmarshal)) ! HttpRequest(host = "ip.jsontest.com")

      val response = expectMsgClass(classOf[HttpResponse[Infos]])
      response.fold(_ => 0, _.size) should not be 0
    }

    "make failed http call and return error" in {
      val failure = HttpAccess.failure("boom")
      system.actorOf(HttpAccessActor(failure)(infoUnmarshal)) ! HttpRequest(host = "bla")

      val response = expectMsgClass(classOf[HttpResponse[Infos]])
      response.fold(_.getMessage, _ => "") should not be empty
    }
  }
}

