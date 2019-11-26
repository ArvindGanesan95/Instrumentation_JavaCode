name := "java-code-instrumentation"

version := "0.1"

scalaVersion := "2.12.8"

sbtVersion := "1.1.2"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.json" % "json" % "20180813",
  "com.google.guava" % "guava" % "12.0",
  "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.10.0",
  "org.apache.commons" % "commons-lang3" % "3.9",
  "commons-io" % "commons-io" % "2.5"
)