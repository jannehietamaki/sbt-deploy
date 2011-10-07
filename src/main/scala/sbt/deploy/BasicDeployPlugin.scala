package sbt.deploy

import sbt._

import sbt.deploy.SecureConnectivityPlugin._
import sbt.deploy.PackageDistPlugin._

import sbt.deploy.DeployInitScriptPlugin._
import sbt.deploy.DeployInitScriptPlugin.{Keys => DISP}

import sbt.deploy.DeployDistPlugin._
import sbt.deploy.DeployDistPlugin.{Keys => DDP}

object BasicDeployPlugin extends Plugin {
  object Keys {
    val deploy = TaskKey[Unit]("deploy")
    // SecureConnectivityPlugin
    val identityFile = SettingKey[File]("identity-filename")
    val user = SettingKey[String]("user")
    val host = SettingKey[String]("host")
    // DeployInitScriptPlugin
    val runInitScriptStart = TaskKey[Unit]("run-init-script-start")
    val runInitScriptStop = TaskKey[Unit]("run-init-script-stop")
    // PackageDistPlugin
    val dependencyPackages = TaskKey[Seq[File]]("dependency-packages")
    // DeployDistPlugin
    val instDirParent = SettingKey[File]("inst-dir-parent")
  }
  import Keys._
  lazy val basicDeploySettings: Seq[Setting[_]] = deployInitScriptSettings ++ deployDistSettings ++ Seq(
    DISP.deployInitScript <<= DISP.deployInitScript dependsOn (runInitScriptStop),
    DDP.deployDist <<= DDP.deployDist dependsOn (DISP.deployInitScript),
    runInitScriptStart <<= runInitScriptStart dependsOn (DDP.deployDist),
    deploy <<= Seq(runInitScriptStart).dependOn
  ) ++ secureConnectivitySettings ++ packageDistSettings
  def ssh(log: Logger, identityFile: File, user: String, host: String, commands: String*) {
    SecureConnectivityPlugin.ssh(log, identityFile, user, host, commands:_*)
  }
  def scp(log: Logger, identityFile: File, user: String, host: String, src: File, dst: String) {
    SecureConnectivityPlugin.scp(log, identityFile, user, host, src, dst)
  }
  def scp(log: Logger, identityFile: File, user: String, host: String, src: File, dst: File) {
    SecureConnectivityPlugin.scp(log, identityFile, user, host, src, dst)
  }
}  
