package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.plugin.Deps.autoImport.testDeps
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt.Keys.{ publishArtifact, testFrameworks, testOptions }
import sbt.TestFrameworks.ScalaTest
import sbt.{ Def, Tests, settingKey }

object Test
  extends Plugin(Deps) {

  object autoImport {
    val scalatestVersion = settingKey[String]("Version of scalatest test-dep to use")
    val testUtilsVersion = settingKey[String]("Version of org.hammerlab:test_utils test-dep to use")

    val publishTestJar = (publishArtifact in sbt.Test := true)

    val scalatest = "org.scalatest" ^^ "scalatest"
    val testUtils = "org.hammerlab" ^^ "test-utils"
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      versions ++=
        Seq(
          scalatest → scalatestVersion.value,
          testUtils → testUtilsVersion.value
        ),

      scalatestVersion := "3.0.0",
      testUtilsVersion := "1.5.1",

      testOptions in sbt.Test += Tests.Argument(ScalaTest, "-oF"),

      // Only use ScalaTest by default; without this, other frameworks get instantiated and can inadvertently mangle
      // test-command-lines/args/classpaths.
      testFrameworks := Seq(ScalaTest),

      // Add hammerlab:test-utils and scalatest as test-deps by default.
      testDeps := Seq(
        testUtils,
        scalatest
      )
    )
}
