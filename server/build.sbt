name := "server"

version := "0.1"

scalaVersion := "2.13.6"

resolvers += Resolver.mavenLocal

libraryDependencies ++= {
  val akka = "com.typesafe.akka"
  val akkaV = "2.6.15"
  val akkaHttpV = "10.2.4"
  val slickV = "3.3.3"

  Seq(
    akka %% "akka-actor-typed" % akkaV,
    akka %% "akka-stream-typed" % akkaV,
    akka %% "akka-cluster-tools" % akkaV,
    akka %% "akka-cluster-sharding-typed" % akkaV,
    akka %% "akka-serialization-jackson" % akkaV,
    akka %% "akka-http" % akkaHttpV,
    akka %% "akka-http-spray-json" % akkaHttpV,
    "ch.megard" %% "akka-http-cors" % "1.1.1",
    "org.flywaydb" % "flyway-core" % "7.11.3",
    akka %% "akka-slf4j" % akkaV,
    "com.typesafe.slick" %% "slick" % slickV,
    "com.typesafe.slick" %% "slick-hikaricp" % slickV,
    "org.postgresql" % "postgresql" % "42.2.23",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scream3r" % "jssc" % "2.8.0",
    "com.google.api-client" % "google-api-client" % "1.32.1",
    "com.google.firebase" % "firebase-admin" % "8.0.0",
    "io.opencensus" % "opencensus-contrib-http-util" % "0.28.3",
    "com.github.tminglei" %% "slick-pg" % "0.19.7",
    "com.github.tminglei" %% "slick-pg_spray-json" % "0.19.7",
//    "com.bot4s" %% "telegram-core" % "4.4.0-RC2",
    "br.com.diegosilva" % "rf_network_jni" % "1.0-SNAPSHOT"
  )
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf")    => MergeStrategy.concat
  case x                             => MergeStrategy.first
}
