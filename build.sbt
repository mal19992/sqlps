name := "padverb"
version := "0.1.0"


scalaVersion := "2.13.8"

target in Compile in doc := baseDirectory.value / "docs/"

libraryDependencies ++= Seq(

  "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
  "org.postgresql" % "postgresql" % "42.3.1",
  "com.sun.mail" % "jakarta.mail" % "2.0.1",
  "commons-validator" % "commons-validator" % "1.7",
  "org.apache.commons" % "commons-text" % "1.9",

  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)

enablePlugins(JavaAppPackaging)

