package sbt.deploy

import sbt._
import Keys._
import PackageDistPlugin._
import SecureConnectivityPlugin._

object DeployDistPlugin extends Plugin {
  val deployDist = TaskKey[Unit]("deploy-dist", "Deploy distribution package")
  val instDirParent = SettingKey[File]("inst-dir-parent")
  val instDir = TaskKey[File]("inst-dir")
  lazy val deployDistSettings = Seq(
    instDir <<= (instDirParent, name) map { (parent, name) =>
      new File(parent, name + "-latest")
    },
    deployDist <<= (streams, identityFile, user, host, packageDist, instDir) map { (out, idFile, user, host, pkgPath, instDir) =>
      scp(out.log, idFile, user, host, pkgPath, instDir.getParent)
      ssh(out.log, idFile, user, host,
        "cd " + instDir.getParent,
        "rm -rf " + instDir.getName,
        "unzip -q -d " + instDir.getName + " " + pkgPath.getName)
    }
  )
}
