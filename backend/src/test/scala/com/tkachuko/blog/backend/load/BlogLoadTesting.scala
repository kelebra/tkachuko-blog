package com.tkachuko.blog.backend.load

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

class BlogLoadTesting extends Simulation with SimulationConfiguration {

  private val scn =
    scenario("Get blog page")
      .repeat(repeats) {
        exec(
          http(_ => "Request /blog")
            .get(s"$host/blog")
            .check(status is 200)
        )
      }

  setUp(scn.inject(atOnceUsers(users)))
}
