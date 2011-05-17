import sbt._

class Project(info: ProjectInfo) extends PluginProject(info) {
  lazy val publishTo = Resolver.file("GitHub Pages", new java.io.File("../sbt-deploy-gh-pages/maven/"))
  override def managedStyle = ManagedStyle.Maven
}
