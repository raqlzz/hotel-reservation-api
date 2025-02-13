name := "hotel-reservation-api"
organization := "com.hotel"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  guice,
  // Slick para manipulação do banco
  "com.typesafe.slick" %% "slick" % "3.3.3",
  // Integração do Play com o Slick
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  // Driver H2 (banco em memória para testes/desenvolvimento)
  "com.h2database" % "h2" % "2.1.214"
)
