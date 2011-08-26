import sbt.FileUtilities._
import sbt._

trait DistributionPackageTask extends BasicScalaProject with DefaultPackageName { 
  lazy val dist = task {
    implicit def pathFinderToPathIterable(pf: PathFinder) = pf.get
    implicit def pathFinderToFileIterable(pf: PathFinder) = pf.get.map(_.asFile)
    log.info("Creating distribution...")
    val distDir = (outputPath / defaultJarBaseName)
    val webapp = (mainSourcePath / "webapp" ##) ** "*"
    createDirectories(distDir :: distDir / "lib" :: Nil , log)
    copy(webapp, distDir / "webapp", log)
    copyFilesFlat(libs, distDir / "lib", log)
    copyFilesFlat(outputPath/defaultJarName, distDir, log)
    createDirectories(distDir :: distDir/ "lib" :: Nil , log)
    zip(List((outputPath ##) / defaultJarBaseName), outputPath / defaultPackageName, true, log)
    None
  } dependsOn(`package`)
  override def packageAction = packageTask(mainClasses +++ mainResources, outputPath, defaultJarName, packageOptions) dependsOn(compile) 
  override def manifestClassPath: Option[String] = Some(libFilenames.foldLeft("")(_ + " lib/" + _))
  private def libFilenames = libs.get.map(_.asFile.getName)
  private def jars: FileFilter = "*.jar"
  private def libs = (runClasspath ** jars) +++ jarsOfProjectDependencies +++ mainDependencies.scalaJars
}
