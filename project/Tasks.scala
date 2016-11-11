import java.awt.Desktop
import java.net.URI

import com.decodified.scalassh.{HostConfig, PasswordLogin, SshClient}
import fr.janalyse.ssh.SSH._
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import sbt.Keys._
import sbt.{Project, _}
import sbtassembly.AssemblyKeys

object Tasks {

  val runLocally = {

    lazy val runWeb = inputKey[Unit]("Runs web server locally")

    runWeb := {
      (runMain in Compile).fullInput(" com.tkachuko.blog.backend.WebServer 127.0.0.1 9090").evaluated
    }
  }

  def deployWebServer(project: Project, log: Logger) = {

    lazy val password = readLine("Enter ssh password : ")
    val (host, user) = ("198.211.104.55", "root")
    val target = s"/$user/blog.jar"

    def stopBlog(): Unit = {
      log.info(s"Stopping running java processes in $host")
      shell(host = host, username = user, password = password) {
        sh =>
          import sh._
          kill(ps().filter(_.cmdline.contains("blog.jar")).map(_.pid))
      }
      log.success(s"Blog process was stopped")
    }

    def copyBlogJar(jar: File) = {
      log.info(s"Copying jar to $host as $target")
      SshClient(
        host = host,
        HostConfig(
          login = PasswordLogin(user, password),
          hostName = host,
          hostKeyVerifier = new PromiscuousVerifier())
      ) match {
        case Right(client) =>
          client.upload(jar.getAbsolutePath, target).fold(message => log.error(message), identity[Unit])
          log.success(s"Successfully uploaded to $host:$target")
        case Left(error) => log.error(error)
      }
    }

    def runBlog() = {
      log.info(s"Running $target on $host")
      shell(host = host, username = user, password = password) {
        sh =>
          import sh._
          execute(s"nohup java -jar $target 0.0.0.0 80 &")
      }
      log.success(s"Started $target at $host")
    }

    def openWebSite() = {
      log.success("Deployment done. Opening blog page...")
      Desktop.getDesktop.browse(new URI("http://tkachuko.info"))
    }

    def deployAndRunJar(jar: File): Unit = {
      stopBlog()
      copyBlogJar(jar)
      runBlog()
      openWebSite()
    }

    (AssemblyKeys.assembly in project) map { file => deployAndRunJar(file) }
  }
}