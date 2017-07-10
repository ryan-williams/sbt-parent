package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Configuration.Provided
import org.hammerlab.sbt.deps.Configurations._
import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.deps.{ Configuration ⇒ Conf }
import org.hammerlab.sbt.plugin.Versions.autoImport.deps
import org.hammerlab.sbt.plugin.Versions.versions
import sbt.Keys.{ excludeDependencies, parallelExecution }
import sbt.{ Def, SbtExclusionRule, SettingsDefinition, settingKey }

object Spark
  extends Plugin {

  import Deps._

  object autoImport {
    val sparkVersion = settingKey[String]("Spark version to use")

    val hadoopVersion = settingKey[String]("Hadoop version to use")

    val addSparkDeps: SettingsDefinition =
      Seq(
        deps ++=
          Seq(
            spark ^ Provided,
            hadoop ^ Provided,
            kryo,
            sparkTests ^ Conf.Test
          )
      )
  }

  private val computedSparkVersion = settingKey[String]("Spark version to use, taking in to account 'spark.version' and 'spark1' env vars")
  private val computedHadoopVersion = settingKey[String]("Hadoop version to use, taking in to account the 'hadoop.version' env var")

  object Deps {
    val spark = (("org.apache.spark" ^^ "spark-core") - ("org.scalatest" ^^ "scalatest"))
    val hadoop = "org.apache.hadoop" ^ "hadoop-client"
    val sparkTests = (("org.hammerlab" ^^ "spark-tests") - ("org.apache.hadoop" ^ "hadoop-client"))
    val kryo = "com.esotericsoftware.kryo" ^ "kryo"
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(

      versions ++=
        Seq(
          hadoop → computedHadoopVersion.value,
          spark → computedSparkVersion.value,
          kryo → "2.24.0",
          sparkTests → "2.0.1"
        ),

      hadoopVersion := "2.7.3",
      computedHadoopVersion := System.getProperty("hadoop.version", hadoopVersion.value),

      sparkVersion := "2.1.0",
      computedSparkVersion := System.getProperty("spark.version", sparkVersion.value),

      // SparkContexts play poorly with parallel test-execution
      parallelExecution in sbt.Test := false,

      // This trans-dep creates a mess in Spark+Hadoop land; just exclude it everywhere by default.
      excludeDependencies += SbtExclusionRule("javax.servlet", "servlet-api")
    )
}