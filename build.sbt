
organization := "org.emis"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-httpx" % sprayV, //+
    "io.spray" %% "spray-client" % sprayV, //+
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "io.spray" %% "spray-json" % "1.3.2",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "org.reactivemongo" %% "reactivemongo" % "0.11.10",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.scalaj" %% "scalaj-http" % "2.3.0"
  )
}

Revolver.settings