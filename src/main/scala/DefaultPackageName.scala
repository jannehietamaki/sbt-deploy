import sbt._

trait DefaultPackageName extends MavenStyleScalaPaths { this: BasicScalaProject =>
  def defaultPackageName = defaultJarBaseName + "-bin.zip"
}
