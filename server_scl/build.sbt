name := "server_scl"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= {
  val akka      = "com.typesafe.akka"
  val akkaV     = "2.6.4"
  val akkaHttpV = "10.1.11"
  val circe     = "io.circe"
  val circeV    = "0.13.0"
  Seq(
    akka  %% "akka-actor"         % akkaV,
    akka  %% "akka-stream"        % akkaV,
    akka  %% "akka-cluster-tools" % akkaV,
    akka  %% "akka-http"          % akkaHttpV,
    circe %% "circe-core"         % circeV,
    circe %% "circe-generic"      % circeV,
    circe %% "circe-parser"       % circeV
  )
}
