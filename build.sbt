import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.packager.Keys._
import sbt.Keys._
import sbt._

val Version = "0.2-SNAPSHOT"
val ScalaVersion = "2.11.7"

def formattingPreferences: FormattingPreferences =
  FormattingPreferences()
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentClassDeclaration, true)

lazy val formatSettings = SbtScalariform.scalariformSettings ++ Seq(
  ScalariformKeys.preferences in Compile := formattingPreferences,
  ScalariformKeys.preferences in Test := formattingPreferences
)

val buildSettings = Seq(
  organization := "org.tmt",
  organizationName := "TMT",
  organizationHomepage := Some(url("http://www.tmt.org")),
  version := Version,
  scalaVersion := ScalaVersion,
  crossPaths := true,
  parallelExecution in Test := false,
  fork := true,
  resolvers += Resolver.typesafeRepo("releases"),
  resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += sbtResolver.value
)

lazy val defaultSettings = buildSettings ++ formatSettings ++ Seq(
  scalacOptions ++= Seq("-target:jvm-1.8", "-encoding", "UTF-8", "-feature", "-deprecation", "-unchecked"),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation")
)

// For standalone applications
def packageSettings(name: String, summary: String, desc: String) = defaultSettings ++
  packagerSettings ++ packageArchetype.java_application ++ Seq(
  version in Rpm := Version,
  rpmRelease := "0",
  rpmVendor := "TMT Common Software",
  rpmUrl := Some("http://www.tmt.org"),
  rpmLicense := Some("MIT"),
  rpmGroup := Some("CSW"),
  packageSummary := summary,
  packageDescription := desc,
  bashScriptExtraDefines ++= Seq(s"addJava -DCSW_VERSION=$Version"),
  bashScriptExtraDefines ++= Seq(s"-Dapplication-name=$name")
)

def compile(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
def provided(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")


// dependencies
val pkg = "org.tmt" %% "pkg" % Version
val containerCmd = "org.tmt" %% "containercmd" % Version
val jeromq = "org.zeromq" % "jeromq" % "0.3.3"
//val jnanomsg = "jnanomsg" % "jnanomsg" % "0.3.2"

lazy val root = (project in file(".")).
  aggregate(assembly1, container1, hcd2, container2)

lazy val assembly1 = project
  .settings(packageSettings("assembly1", "Assembly Demo", "Example assembly"): _*)
  .settings(libraryDependencies ++= Seq(pkg))

lazy val container1 = project
  .settings(packageSettings("container1", "Container demo", "Example container"): _*)
  .settings(libraryDependencies ++= Seq(containerCmd)) dependsOn assembly1

lazy val hcd2 = project
  .settings(packageSettings("hcd2", "HCD demo", "Example HCD"): _*)
  .settings(libraryDependencies ++= Seq(pkg, jeromq))

lazy val container2 = project
  .settings(packageSettings("container2", "Container demo", "Example container"): _*)
  .settings(libraryDependencies ++= Seq(containerCmd)) dependsOn hcd2
