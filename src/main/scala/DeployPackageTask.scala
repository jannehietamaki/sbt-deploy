import sbt._

trait DeployPackageTask extends DistributionPackageTask with Connection { this: BasicScalaProject =>
  def deploy = task {
    copyFileToRemoteServer((outputPath / defaultPackageName).toString, installationDir, hostname)
    preInstallation
    executeRemoteCommands(hostname,
      "cd " + installationDir,
      "rm -rf " + defaultJarBaseName + " " + latestDir,
      "unzip -q " + defaultPackageName,
      "ln -s " + defaultJarBaseName + " " + latestDir,
      "ln -s " + defaultJarName + " " + latestDir + "/" + latestDir + ".jar"
    )
    postInstallation
    None
  } dependsOn(`dist`)
  def latestDir: String = projectName.value
  def installationDir: String 
  def preInstallation: Unit
  def postInstallation: Unit
}
