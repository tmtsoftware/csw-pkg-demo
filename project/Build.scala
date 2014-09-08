import sbt._
import Keys._
import com.typesafe.sbt.packager.Keys._


// This is the top level build object used by sbt.
object Build extends Build {
  import Settings._
  import Dependencies._

  lazy val container1 = project
    .settings(packageSettings: _*)
    .settings(bashScriptExtraDefines ++= Seq("addJava -Dapplication-name=container1 -Dcsw.extjs.root="
      + file("../csw-extjs").absolutePath))
    .settings(libraryDependencies ++=
      provided(akkaActor) ++
      compile(akkaKernel, akkaRemote, pkg, log)
    )

  lazy val container2 = project
    .settings(packageSettings: _*)
    .settings(bashScriptExtraDefines ++= Seq("addJava -Dapplication-name=container2"))
    .settings(libraryDependencies ++=
      provided(akkaActor) ++
      compile(akkaKernel, akkaRemote, jeromq, pkg, log)
    )
}
