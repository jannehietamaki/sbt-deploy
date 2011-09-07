package sbt.deploy

import sbt._
import Keys._

object PackageDistPlugin extends Plugin {
  val packageDist = TaskKey[File]("package-dist", "Create a distribution package")
  val distPackagePath = TaskKey[File]("dist-package-filename", "The distribution package filename")
  val distPackageContents = TaskKey[Seq[(File, String)]]("dist-package-contents", "The contents of the distribution package")
  val dependencyJarFiles = TaskKey[Seq[(File, String)]]("dependency-jar-files")
  val dependencyPackages = TaskKey[Seq[File]]("dependency-packages")
  val projectJarFile = packageBin in Compile
  lazy val packageDistSettings = Seq(
    packageOptions <+= dependencyJarFiles map { files =>
      import java.util.jar.Attributes.Name.CLASS_PATH
      Package.ManifestAttributes((CLASS_PATH, files.map(_._2).mkString(" ")))
    },
    packageOptions <+= mainClass map { mainClass =>
      Package.MainClass(mainClass.getOrElse(""))
    },
    packageDist <<= (streams, distPackageContents, distPackagePath) map { (out, packageContents, packagePath) =>
      out.log.info("Packaging distribution " + packagePath + " ...")
      IO.zip(packageContents, packagePath)
      packagePath
    },
    distPackageContents <<= (dependencyJarFiles, projectJarFile) map { (depJarFiles, jarFile)  =>
      Seq((jarFile, jarFile.getName)) ++ depJarFiles
    },
    dependencyJarFiles <<= (dependencyClasspath in Runtime, dependencyPackages) map { (classpath, dependencyPackages) =>
      dependencyPackages ++ classpath.files.filter(_.getName.endsWith(".jar")) x Path.flat map { case (src, dst) =>
        (src, "lib/" + dst)
      }
    },
    distPackagePath <<= packageBin in Compile map { file =>
      new File(file.getAbsolutePath.replaceAll(".jar$", "") + "-bin.zip")
    }
  )
}
