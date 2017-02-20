package com.tkachuko.blog.backend.load

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

class IndexLoadTesting extends Simulation with SimulationConfiguration {

  private val scn =
    scenario("Get index.html page")
      .repeat(repeats) {
        exec(
          http(_ => "Request /")
            .get(host)
            .check(status is 200)
        )
      }

  setUp(scn.inject(atOnceUsers(users)))
}
