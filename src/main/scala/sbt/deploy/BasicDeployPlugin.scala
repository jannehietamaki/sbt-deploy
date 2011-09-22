package sbt.deploy

import sbt._

import sbt.deploy.SecureConnectivityPlugin._
import sbt.deploy.DeployInitScriptPlugin._
import sbt.deploy.PackageDistPlugin._
import sbt.deploy.DeployDistPlugin._

object BasicDeployPlugin extends Plugin {
  val deploy = TaskKey[Unit]("deploy")
  lazy val basicDeploySettings: Seq[Setting[_]] = deployInitScriptSettings ++ deployDistSettings ++ Seq(
    deployInitScript <<= deployInitScript dependsOn (runInitScriptStop),
    deployDist <<= deployDist dependsOn (deployInitScript),
    runInitScriptStart <<= runInitScriptStart dependsOn (deployDist),
    deploy <<= Seq(runInitScriptStart).dependOn
  ) ++ secureConnectivitySettings ++ packageDistSettings
}  
