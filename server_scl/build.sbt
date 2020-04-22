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
    akka  %% "akka-actor-typed"                 % akkaV,
    akka  %% "akka-stream-typed"                % akkaV,
    akka  %% "akka-cluster-tools"               % akkaV,
    akka  %% "akka-cluster-sharding-typed"      % akkaV,
    akka  %% "akka-serialization-jackson"       % akkaV,
    akka  %% "akka-http"                        % akkaHttpV,
    akka  %% "akka-http-spray-json"             % akkaHttpV,
    akka  %% "akka-slf4j"                       % akkaV,
    circe %% "circe-core"                       % circeV,
    circe %% "circe-generic"                    % circeV,
    circe %% "circe-parser"                     % circeV,
    "ch.qos.logback" % "logback-classic"        % "1.2.3",
    "org.scream3r" % "jssc" % "2.8.0"
  )
}
