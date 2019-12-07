name := "object-csv"

version := "0.3"

scalaVersion := "2.11.7"

publishMavenStyle := true

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5"
)

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

organization := "com.gingersoftware"

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
      <connection>scm:git@github.com:dorony/object-csv.git</connection>
    </scm>
    <developers>
      <developer>
        <id>dorony</id>
        <name>Doron Yaacoby</name>
        <url>http://blogs.microsoft.co.il/blogs/dorony</url>
      </developer>
    </developers>)