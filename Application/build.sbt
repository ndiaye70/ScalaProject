ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "Back-End"
  )


libraryDependencies ++= Seq(
  "com.auth0" % "java-jwt" % "4.4.0",
  "com.github.jwt-scala" %% "jwt-play" % "10.0.0", // Pour JWT + Play
"com.typesafe.akka" %% "akka-http"           % "10.5.3",
  "com.typesafe.akka" %% "akka-stream"         % "2.8.7",
  "com.typesafe.slick" %% "slick"              % "3.5.2",
  "com.typesafe.slick" %% "slick-hikaricp"     % "3.5.2",
  "org.postgresql"     %  "postgresql"         % "42.5.0",
  "ch.qos.logback"     %  "logback-classic"    % "1.4.12",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
  "io.spray"          %% "spray-json"          % "1.3.6"
)
