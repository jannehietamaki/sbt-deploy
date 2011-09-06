package sbt.deploy

import sbt._
import Keys._
import PackageDistPlugin._

object DeployDistPlugin extends Plugin with SecureConnectivity {
  val deployDist = TaskKey[Unit]("deploy-dist", "Deploy distribution package to a remote server")
  val beforeDeploy = TaskKey[Unit]("before-deploy", "A task that is run before deploying a distribution package")
  val afterDeploy = TaskKey[Unit]("after-deploy", "A task that is run after deplyoing a distribution package")
  val deploy = TaskKey[Unit]("deploy", "Deploy distribution package to a remote server")
  val identityFile = SettingKey[File]("identity-filename")
  val instDir = SettingKey[File]("inst-dir")
  val user = SettingKey[String]("user")
  val host = SettingKey[String]("host")
  lazy val deployDistSettings = Seq(
    deploy <<= (streams, identityFile, user, host, packageDist, instDir) map { (out, idFile, user, host, pkgPath, instDir) =>
      scp(out.log, idFile, user, host, pkgPath, instDir)
      ssh(out.log, idFile, user, host,
        "cd " + instDir.getParent,
        "rm -rf " + instDir.getName,
        "unzip -q -d " + instDir.getName + " " + pkgPath.getName
      )
    },
    deployDist <<= afterDeploy dependsOn (deploy),
    deploy <<= deploy.dependsOn (beforeDeploy)
  )
}