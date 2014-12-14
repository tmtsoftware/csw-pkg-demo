import sbt._
import Keys._
import com.typesafe.sbt.packager.Keys._


// This is the top level build object used by sbt.
object Build extends Build {
  import Settings._
  import Dependencies._

  lazy val container1 = project
    .settings(packageSettings: _*)
    .settings(mainClass in Compile := Some("csw.services.pkg.ContainerCmd"))
    .settings(bashScriptExtraDefines ++= Seq("addJava -Dcsw.extjs.root="
      + file("../csw-extjs").absolutePath))
    .settings(libraryDependencies ++=
      provided(akkaActor) ++
      compile(akkaRemote, pkg, log)
    )

  lazy val container2 = project
    .settings(packageSettings: _*)
    .settings(mainClass in Compile := Some("csw.services.pkg.ContainerCmd"))
    .settings(libraryDependencies ++=
      provided(akkaActor) ++
      compile(akkaRemote, jeromq, pkg, log)
    )
}
