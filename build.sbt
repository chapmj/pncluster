lazy val commonSettings = Seq(
	version := "1.0",
	scalaVersion := "2.12.8",
	scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation"),
	resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
	libraryDependencies ++= Seq(
		"com.typesafe.akka" %% "akka-actor" % "2.5.23",
		"com.typesafe.akka" %% "akka-remote" % "2.5.23",
		"com.typesafe.akka" %% "akka-cluster" % "2.5.23"))
    
lazy val root = (project in file("."))
	.aggregate(pncluster)

lazy val pncluster = (project in file("pncluster"))
	.settings(commonSettings: _*)
	.settings(name := "pncluster")

enablePlugins(DockerPlugin)

//mainClass in Compile := Some("com.example.QuickstartServer")
/* Run the following to build docker image:
 *     sbt docker:publishLocal
*/
