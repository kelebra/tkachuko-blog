import sbt.Keys.{name, organization, version}
import sbt.{File, Project}
import sbtassembly.AssemblyPlugin.autoImport.assembly
import sbtdocker.DockerPlugin.autoImport.{docker, dockerfile, imageNames}
import sbtdocker.mutable.Dockerfile
import sbtdocker.{DockerPlugin, ImageName}

object DockerConfiguration {

  implicit class DockerProject(project: Project) {

    def dockerWeb(host: String, port: Int): Project =
      project
        .enablePlugins(DockerPlugin)
        .settings(
          dockerfile in docker := {
            val artifact: File = assembly.value
            val artifactTargetPath = s"/app/${artifact.getName}"

            // Run it with docker run -p 80:80 <image name>
            new Dockerfile {
              from("java")
              add(artifact, artifactTargetPath)
              expose(port)
              entryPoint("java", "-jar", artifactTargetPath, host, port.toString)
            }
          },
          imageNames in docker := Seq(
            ImageName(s"${organization.value}-${name.value}:v${version.value}")
          )
        )
  }

}