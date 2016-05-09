import sbt.Keys._
import sbt._

import Dependencies._
import Settings._

// -- Scala Projects --
lazy val assembly1 = project
  .enablePlugins(JavaAppPackaging)
  .settings(packageSettings("assembly1", "Assembly Demo", "Example assembly"): _*)
  .settings(libraryDependencies ++= Seq(pkg))

lazy val container1 = project
  .enablePlugins(JavaAppPackaging)
  .settings(packageSettings("container1", "Container demo", "Example container"): _*)
  .settings(libraryDependencies ++= Seq(containerCmd)) dependsOn assembly1

lazy val hcd2 = project
  .enablePlugins(JavaAppPackaging)
  .settings(packageSettings("hcd2", "HCD demo", "Example HCD"): _*)
  .settings(libraryDependencies ++= Seq(pkg, jeromq))

lazy val container2 = project
  .enablePlugins(JavaAppPackaging)
  .settings(packageSettings("container2", "Container demo", "Example container"): _*)
  .settings(libraryDependencies ++= Seq(containerCmd)) dependsOn hcd2

// -- Root Project --
lazy val cswPkgDemo = (project in file("."))
  .settings(name := "CSW Package Demo")
  .aggregate(assembly1, container1, hcd2, container2)
