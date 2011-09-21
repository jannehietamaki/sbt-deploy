package sbt.deploy

import sbt._
import Keys._
import PackageDistPlugin._
import SecureConnectivityPlugin._

object DeployDistPlugin extends Plugin {
  val deployDist = TaskKey[Unit]("deploy-dist", "Deploy distribution package")
  val instPkgPath = TaskKey[File]("inst-pkg-path")
  val instDirParent = SettingKey[File]("inst-dir-parent")
  val instDir = TaskKey[File]("inst-dir")
  val latestInstDir = TaskKey[File]("latest-inst-dir")
  val instJarPath = TaskKey[File]("inst-jar-path")
  val latestInstJarPath = TaskKey[File]("latest-inst-jar-path")
  lazy val deployDistSettings = Seq(
    instDir <<= (instDirParent, name, version, scalaVersion) map { (parent, name, version, scalaVersion) =>
      new File(parent, "%s_%s-%s".format(name, scalaVersion, version))
    },
    instPkgPath <<= (instDir, packageDist) map { (instDir, pkgPath) =>
      new File(instDir, pkgPath.getName)
    },
    latestInstDir <<= (instDir, name) map { (instDir, name) =>
      new File(instDir.getParent, name)
    },
    instJarPath <<= (instDir) map { (instDir) =>
      new File(instDir, instDir.getName + ".jar")
    },
    latestInstJarPath <<= (latestInstDir, name) map { (latestInstDir, name) =>
      new File(latestInstDir, name + ".jar")
    },
    deployDist <<= (streams, identityFile, user, host, packageDist, instDir, latestInstDir, instJarPath, latestInstJarPath) map { 
        (out, idFile, user, host, pkgPath, instDir, latestInstDir, instJarPath, latestInstJarPath) =>
      scp(out.log, idFile, user, host, pkgPath, instDir.getParent)
      ssh(out.log, idFile, user, host,
        "cd " + instDir.getParent,
        "rm -rf " + instDir.getName + " " + latestInstDir,
        "unzip -q -d " + instDir.getName + " " + pkgPath.getName,
        "ln -s " + instJarPath.getParent + " " + latestInstJarPath.getParent,
        "ln -s " + instJarPath + " " + latestInstJarPath
      )
    }
  )
}
