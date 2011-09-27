sbtPlugin := true

organization := "fi.reaktor"

name := "sbt-deploy"

version := "0.3.0-SNAPSHOT"

scalacOptions := Seq("-deprecation", "-unchecked")

publishTo := Some(Resolver.file("Github Pages", file("../sbt-deploy-gh-pages/maven/")))
