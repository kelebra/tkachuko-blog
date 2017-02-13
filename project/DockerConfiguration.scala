import sbt.Keys.{name, organization}
import sbt.{File, Project}
import sbtassembly.AssemblyPlugin.autoImport.assembly
import sbtdocker.DockerPlugin.autoImport.{docker, dockerfile, imageNames}
import sbtdocker.mutable.Dockerfile
import sbtdocker.{DockerPlugin, ImageName}

object DockerConfiguration {

  implicit class DockerProject(project: Project) {

    // Run it with docker run -p 80:80 -d <image name>
    def dockerWeb(host: String, port: Int): Project =
      project
        .enablePlugins(DockerPlugin)
        .settings(
          dockerfile in docker := {
            val artifact: File = assembly.value
            val artifactTargetPath = s"/app/${artifact.getName}"

            new Dockerfile {
              from("java")
              add(artifact, artifactTargetPath)
              expose(port)
              entryPoint("nohup", "java", "-jar", artifactTargetPath, host, port.toString, "&")
            }
          },
          imageNames in docker := Seq(
            ImageName(s"kelebra/${organization.value}-${name.value}:latest")
          )
        )
  }

}