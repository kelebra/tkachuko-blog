package com.tkachuko.blog.repository.source.http

import akka.actor.ActorSystem
import com.tkachuko.blog.common.ActorSpec
import io.circe.Decoder
import io.circe.generic.semiauto._

class HttpAccessActorTest extends ActorSpec(ActorSystem("HttpAccessActorTest")) {

  case class Response(ip: String)

  implicit val decoder: Decoder[Response] = deriveDecoder[Response]
  import system.dispatcher

  val mock: HttpAccess = HttpAccess.static("{\"ip\": \"2602:306:8bd7:cd20:ac87:afb5:21e1:8dc8\"}")

  "Http actor" should {

    "make http call and deserialize json" in {

      system.actorOf(HttpAccessActor(mock)) ! Request("http", "ip.jsontest.com")

      val response = expectMsgClass(classOf[Response])
      response.ip should not be empty
    }
  }
}
