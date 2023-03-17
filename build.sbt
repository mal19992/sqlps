name := "sqlps"
version := "0.1.0"


scalaVersion := "2.13.10"

Compile / doc := baseDirectory.value / "docs/api/"

libraryDependencies ++= Seq(

  "com.typesafe.play" %% "play-json" % "2.10.0-RC7",
  "org.postgresql" % "postgresql" % "42.5.0",
  "com.sun.mail" % "jakarta.mail" % "2.0.1",
  "commons-validator" % "commons-validator" % "1.7",
  "org.apache.commons" % "commons-text" % "1.9",

  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)

enablePlugins(JavaAppPackaging)

