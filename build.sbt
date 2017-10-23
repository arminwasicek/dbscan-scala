organization := "com.esri"
name := "dbscan-scala"
version := "0.4"
description := "DBSCAN implementation in Scala using Apache Commons Math"
homepage := Some(url(s"https://github.com/mraad/${name.value}#readme"))
isSnapshot := true

scalaVersion := "2.10.5"
crossScalaVersions := Seq("2.10.5", "2.11.6")

licenses +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6"

publishMavenStyle := true

publishArtifact in Test := false

pomExtra :=
  <url>https://github.com/mraad/dbscan-scala</url>
    <licenses>
      <license>
        <name>Apache License, Verision 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:mraad/dbscan-scala.git</url>
      <connection>scm:git:git@github.com:mraad/dbscan-scala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>mraad</id>
        <name>Mansour Raad</name>
        <url>https://github.com/mraad</url>
        <email>mraad@esri.com</email>
      </developer>
    </developers>

