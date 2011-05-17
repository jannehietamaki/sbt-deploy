import sbt._

trait DeployStartJavaTask extends Connection with MavenStyleScalaPaths { this: BasicScalaProject =>
  lazy val deployStartJava = task {
    copyFileToRemoteServer(mainSourcePath / "init.d" / startJavaScriptFilename, "/tmp", hostname)
    executeRemoteCommands(hostname, 
      "sudo mv /tmp/" + startJavaScriptFilename + " " + installationDir,
      "sudo chown " + user + "." + user + " " + startJavaPath,
      "sudo chmod 755 " + startJavaPath
    )
    None
  }
  def startJavaPath = installationDir + "/" + startJavaScriptFilename
  def startJavaScriptFilename = "start-java.sh"
  def installationDir: String
}
