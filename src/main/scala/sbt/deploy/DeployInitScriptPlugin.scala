package sbt.deploy

import sbt._
import Keys._
import SecureConnectivityPlugin._

object DeployInitScriptPlugin extends Plugin {
  object Keys {
    val runInitScriptStart = TaskKey[Unit]("run-init-script-start")
    val runInitScriptStop = TaskKey[Unit]("run-init-script-stop")
    val deployInitScript = TaskKey[Unit]("deploy-init-script")
    val initScriptPath = TaskKey[File]("init-script-path")
    val tmpInitScriptPath = TaskKey[File]("tmp-init-script-path")
  }
  import Keys._
  lazy val deployInitScriptSettings = Seq(
    initScriptPath <<= (sourceDirectory, name) map { (sourceDirectory, name) =>
      new File(sourceDirectory, "init.d/" + name)
    },
    tmpInitScriptPath <<= initScriptPath map { initScriptPath =>
      new File("/tmp", initScriptPath.getName + System.currentTimeMillis)
    },
    runInitScriptStop <<= (streams, identityFile, user, host, initScriptPath) map { 
        (out, idFile, user, host, initScriptPath) =>
      val etcInitScriptPath = new File("/etc/init.d/", initScriptPath.getName)
      ssh(out.log, idFile, user, host, "sudo " + etcInitScriptPath + " stop")
    },
    runInitScriptStart <<= (streams, identityFile, user, host, initScriptPath, tmpInitScriptPath) map {
        (out, idFile, user, host, initScriptPath, tmpInitScriptPath) =>
      val etcInitScriptPath = new File("/etc/init.d/", initScriptPath.getName)
      ssh(out.log, idFile, user, host, "sudo " + etcInitScriptPath + " start")
    },
    deployInitScript <<= (streams, identityFile, user, host, initScriptPath, tmpInitScriptPath) map { 
        (out, idFile, user, host, initScriptPath, tmpInitScriptPath) =>
      val etcInitScriptPath = new File("/etc/init.d/", initScriptPath.getName)
      scp(out.log, idFile, user, host, initScriptPath, tmpInitScriptPath)
      ssh(out.log, idFile, user, host,
        "sudo mv " + tmpInitScriptPath + " " + etcInitScriptPath,
        "sudo chown root.root " + etcInitScriptPath,
        "sudo chmod 750 " + etcInitScriptPath
      )
    }
  )
}
