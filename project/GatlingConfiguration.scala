import io.gatling.sbt.GatlingPlugin
import sbt.Project

object GatlingConfiguration {

  implicit class GatlingProject(project: Project) {

    def gatling: Project = project.enablePlugins(GatlingPlugin)
  }
}