name := "Instrumenter"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.2",
  // https://mvnrepository.com/artifact/com.typesafe.scala-logging/scala-logging
  // https://mvnrepository.com/artifact/com.typesafe.scala-logging/scala-logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",


//"com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.json" % "json" % "20180813",
  "com.google.guava" % "guava" % "12.0",
  "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.10.0",
  "org.apache.commons" % "commons-lang3" % "3.9",
  "commons-io" % "commons-io" % "2.5",
  "org.apache.logging.log4j" % "log4j-api" % "2.12.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.12.1",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.12.1"

)

mainClass in (Compile, run) := Some("com.instrumentation.InstrumentationDriver")