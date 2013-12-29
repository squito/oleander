import sbt._
import Keys._

object SparkBuild extends Build {

  lazy val root = Project("root", file("."), settings = rootSettings) aggregate(allProjects: _*)
  lazy val allProjects = Seq[ProjectReference](
    core, macros, macrotests, demos)


  lazy val core = Project("core", file("core"), settings = coreSettings)

  def sharedSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.10.2",
    organization := "com.imranrashid",
    scalacOptions := Seq("-deprecation", "-unchecked", "-optimize"),
    unmanagedBase <<= baseDirectory { base => base / "unmanaged" },
    retrieveManaged := true,
    transitiveClassifiers in Scope.GlobalScope := Seq("sources"),
    publishTo <<= baseDirectory { base => Some(Resolver.file("Local", base / "target" / "maven" asFile)(Patterns(true, Resolver.mavenStyleBasePattern))) },
    libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
    )
  )

  def rootSettings = sharedSettings

  val slf4jVersion = "1.6.1"

  def coreSettings = sharedSettings ++ Seq(
    name := "Oleander",
    resolvers ++= Seq(
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
      Resolver.sonatypeRepo("snapshots"),
      "Quantifind External Snapshots" at "http://repo.quantifind.com/content/repositories/ext-snapshots/"
    ),
    addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "log4j" % "log4j" % "1.2.16",
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "org.slf4j" % "slf4j-log4j12" % slf4jVersion
    )
  )

  lazy val macros = Project("macros", file("macros"), settings = macrosSettings) dependsOn(core)
  
  lazy val macrotests = Project("macrotests", file("macrotests"), settings = macrotestsSettings) dependsOn(macros)

  lazy val demos = Project("demos", file("demos"), settings = demoSettings) dependsOn(macros)

  def macrosSettings = coreSettings ++ Seq(
    name := "macros",
    libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _)
  )

  def macrotestsSettings = coreSettings ++ Seq(
    name := "macrotests"
  )
  
  def demoSettings = coreSettings ++ Seq(
    name := "demos",
    libraryDependencies ++= Seq(
      // Serialization stuff
      "com.twitter" % "chill-bijection_2.10" % "0.3.3",
      "org.xerial.snappy" % "snappy-java" % "1.1.0-M3",
      //we really need to release sumac 0.2 so this can reference a normal release ...
      "com.quantifind" % "sumac_2.10" % "0.2.7023969-SNAPSHOT"
    )
  )
  
}
