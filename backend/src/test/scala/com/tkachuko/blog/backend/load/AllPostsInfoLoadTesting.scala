package com.tkachuko.blog.backend.load

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class AllPostsInfoLoadTesting extends Simulation with SimulationConfiguration {
  private val scn =
    scenario("Load short posts info as JSON")
      .repeat(repeats) {
        exec(
          http(_ => "Request /posts/info")
            .get(s"$host/posts/info")
            .check(status is 200)
        )
      }

  setUp(scn.inject(atOnceUsers(users)))
}
