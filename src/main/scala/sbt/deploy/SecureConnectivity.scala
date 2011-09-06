package sbt.deploy

import sbt._

trait SecureConnectivity {
  def ssh(log: Logger, identityFile: File, user: String, host: String, commands: String*) {
    val chain = commands(0) + commands.toList.tail.foldLeft("")(_ + " && " + _)
    log.info("Executing command chain '" + chain + "' on remote server " + host + "...")
    "ssh -i " + identityFile + " " + user + "@" + host + " " + chain ! log
  }
  def scp(log: Logger, identityFile: File, user: String, host: String, src: File, dst: File) {
    log.info("Copying file " + src + " to " + host + ":" + dst + "...")
    "scp -r -i " + identityFile + " " + src + " " + user + "@" + host + ":" + dst ! log
  }
}
