package com.tkachuko.blog.repository.source.http

import akka.actor.ActorSystem
import com.tkachuko.blog.common.ActorSpec
import io.circe.Decoder
import io.circe.generic.semiauto._

class HttpAccessActorTest extends ActorSpec(ActorSystem("HttpAccessActorTest")) {

  case class IpAddress(ip: String)

  implicit val decoder: Decoder[IpAddress] = deriveDecoder[IpAddress]

  import system.dispatcher

  val success: HttpAccess = HttpAccess.success("{\"ip\": \"2602:306:8bd7:cd20:ac87:afb5:21e1:8dc8\"}")
  val failure: HttpAccess = HttpAccess.failure("boom")

  "Http actor" should {

    "make successful http call and deserialize json" in {

      system.actorOf(HttpAccessActor(success)) ! HttpRequest(host = "ip.jsontest.com")

      val response = expectMsgClass(classOf[HttpResponse[IpAddress]])
      response.fold(_ => "", _.ip) should not be empty
    }

    "make failed http call and return error" in {

      system.actorOf(HttpAccessActor(failure)) ! HttpRequest(host = "bla")
      val response = expectMsgClass(classOf[HttpResponse[IpAddress]])
      response.fold(_.getMessage, _ => "") should not be empty
    }
  }
}
