import sbt._
import Keys._

object SparkBuild extends Build {
  lazy val core = Project("core", file("."), settings = coreSettings)

  def sharedSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.9.3",
    organization := "com.imranrashid",
    scalacOptions := Seq("-deprecation", "-unchecked", "-optimize"),
    unmanagedJars in Compile <<= baseDirectory map { base => (base / "lib" ** "*.jar").classpath },
    retrieveManaged := true,
    transitiveClassifiers in Scope.GlobalScope := Seq("sources"),
    publishTo <<= baseDirectory { base => Some(Resolver.file("Local", base / "target" / "maven" asFile)(Patterns(true, Resolver.mavenStyleBasePattern))) },
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "1.9.1" % "test"
    )
  )

  val slf4jVersion = "1.6.1"

  def coreSettings = sharedSettings ++ Seq(
    name := "Oleander",
    resolvers ++= Seq(
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/"
    ),
    libraryDependencies ++= Seq(
      "log4j" % "log4j" % "1.2.16",
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "org.slf4j" % "slf4j-log4j12" % slf4jVersion,
      "org.scalatest" %% "scalatest" % "1.9.1" % "test"
    )
  )

}
