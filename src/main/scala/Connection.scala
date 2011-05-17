import sbt.Process._
import sbt._

trait Connection { this: BasicScalaProject =>
  def copyFileToRemoteServer(file: Path, targetDirectory: String, hostname: String) {
    copyFileToRemoteServer(file.toString, targetDirectory, hostname)
  }
  def copyFileToRemoteServer(file: String, targetDirectory: String, hostname: String) {
    val newName = file.substring(file.lastIndexOf("/") + 1)
    copyFileToRemoteServer(file, targetDirectory, newName, hostname)
  }
  def copyFileToRemoteServer(file: String, targetDirectory: String, newName: String, hostname: String) {
    log.info("Copying local file " + file + " to " + hostname + ":" + targetDirectory + "/" + newName + " ...")
    "scp -r -i " + identityFilename + " " + file + " " + user + "@" + hostname + ":" + targetDirectory + "/" + newName ! log
  }
  def executeRemoteCommands(hostname: String, commands: String*) = {
    val commandChain = commands(0) + commands.toList.tail.foldLeft("")(_ + " && " + _)
    log.info("Executing command chain '" + commandChain + "' on remote server " + hostname + "...")
    "ssh -i " + identityFilename + " " + user + "@" + hostname + " " + commandChain ! log
  }
  def identityFilename: String = ".ssh/id_rsa"
  def hostname: String
  def user: String
}
