name := "object-csv"

version := "0.1"

scalaVersion := "2.11.0"

publishMavenStyle := true

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.5" % "test"

libraryDependencies += "com.github.tototoshi" % "scala-csv_2.10" % "1.0.0"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "org.scala-lang" % "scala-parser-combinators" % "2.11.0-M4"

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/dorony/object-csv</url>
    <licenses>
      <license>
        <name>Apache 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:dorony/object-csv.git</url>
      <connection>scm:git:git@github.com:dorony/object-csv.git</connection>
    </scm>
    <developers>
      <developer>
        <id>dorony</id>
        <name>Doron Yaacoby</name>
        <url>http://github.com/dorony</url>
      </developer>
    </developers>)
