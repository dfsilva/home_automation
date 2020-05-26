name := "server"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += Resolver.mavenLocal

libraryDependencies ++= {
  val akka = "com.typesafe.akka"
  val akkaV = "2.6.5"
  val akkaHttpV = "10.1.11"
  val slickV = "3.3.2"
  Seq(
    akka %% "akka-actor-typed" % akkaV,
    akka %% "akka-stream-typed" % akkaV,
    akka %% "akka-cluster-tools" % akkaV,
    akka %% "akka-cluster-sharding-typed" % akkaV,
    akka %% "akka-serialization-jackson" % akkaV,
    akka %% "akka-http" % akkaHttpV,
    akka %% "akka-http-spray-json" % akkaHttpV,
    akka %% "akka-slf4j" % akkaV,
    "com.typesafe.slick" %% "slick" % slickV,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scream3r" % "jssc" % "2.8.0",
    "br.com.diegosilva" % "rf_network_jni" % "1.0-SNAPSHOT"
  )
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case x => MergeStrategy.first
}
