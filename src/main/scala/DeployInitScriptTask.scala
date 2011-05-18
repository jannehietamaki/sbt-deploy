import sbt._

trait DeployInitScriptTask extends Connection with MavenStyleScalaPaths { this: BasicScalaProject =>
  def deployInitScript = task {
    copyFileToRemoteServer(mainSourcePath / "init.d" / initScriptFilename, "/tmp", hostname)
    executeRemoteCommands(hostname, 
      "sudo mv /tmp/" + initScriptFilename + " /etc/init.d",
      "sudo chown root.root /etc/init.d/" + initScriptFilename,
      "sudo chmod 750 /etc/init.d/" + initScriptFilename
    )
    None
  }
  def preInstallation: Unit = runInitScriptStop
  def postInstallation: Unit = runInitScriptStart
  def runInitScriptStart = executeRemoteCommands(hostname, "sudo /etc/init.d/" + projectName.value + " start")
  def runInitScriptStop = executeRemoteCommands(hostname, "sudo /etc/init.d/" + projectName.value + " stop")
  def initScriptFilename: String = projectName.value
}
