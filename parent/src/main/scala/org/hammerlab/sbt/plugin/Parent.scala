package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.{ Group, SnapshotOps }
import org.hammerlab.sbt.plugin.Spark.autoImport.{ hadoop, sparkVersion }
import org.hammerlab.sbt.plugin.Test.autoImport.scalatest
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt._

object Parent
  extends Plugin(
    Deps,
    GitHub,
    Maven,
    Spark,
    Versions
  ) {

  import Group._

  object autoImport
    extends SnapshotOps {

    val args4j = "args4j" ^ "args4j"
    val bdg_formats = "org.bdgenomics.bdg-formats" ^ "bdg-formats"
    val bdg_utils_intervalrdd = "org.bdgenomics.utils" ^^ "utils-intervalrdd-spark2"
    val bdg_utils_io = "org.bdgenomics.utils" ^^ "utils-io-spark2"
    val bdg_utils_metrics = "org.bdgenomics.utils" ^^ "utils-metrics-spark2"
    val bdg_utils_misc = "org.bdgenomics.utils" ^^ "utils-misc-spark2"
    val breeze = "org.scalanlp" ^^ "breeze"
    val case_app = "com.github.alexarchambault" ^^ "case-app"
    val cats = "org.typelevel" ^^ "cats-core"
    val commons_io = "commons-io" ^ "commons-io"
    val commons_math = "org.apache.commons" ^ "commons-math3"
    val guava = "com.google.guava" ^ "guava"
    val seqdoop_hadoop_bam = ("org.seqdoop" ^ "hadoop-bam") - hadoop
    val htsjdk = ("com.github.samtools" ^ "htsjdk") - ("org.xerial.snappy" ^ "snappy-java")
    val kittens = "org.typelevel" ^^ "kittens"
    val log4j = "org.slf4j" ^ "slf4j-log4j12"
    val mllib = ("org.apache.spark" ^^ "spark-mllib") - scalatest
    val parquet_avro = "org.apache.parquet" ^ "parquet-avro"
    val quinine_core = ("org.bdgenomics.quinine" ^^ "quinine-core") - ("org.bdgenomics.adam" ^^ "adam-core")
    val scalautils = "org.scalautils" ^^ "scalautils"
    val shapeless = "com.chuusai" ^^ "shapeless"
    val slf4j = "org.clapper" ^^ "grizzled-slf4j"
    val spire = "org.spire-math" ^^ "spire"

    val bdgUtilsVersion = settingKey[String]("org.bdgenomics.utils version to use")
  }

  import autoImport._

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] =
    Seq(
      bdgUtilsVersion := "0.2.13",

      versions ++= Seq(
        args4j                → "2.33",
        bdg_formats           → "0.10.1",
        bdg_utils_intervalrdd → bdgUtilsVersion.value,
        bdg_utils_io          → bdgUtilsVersion.value,
        bdg_utils_metrics     → bdgUtilsVersion.value,
        bdg_utils_misc        → bdgUtilsVersion.value,
        breeze                → "0.13.2",
        cats                  → "1.0.1",
        commons_io            → "2.5",
        commons_math          → "3.6.1",
        case_app              → "1.2.0",
        guava                 → "19.0",
        htsjdk                → "2.9.1",
        kittens               → "1.0.0-RC2",
        log4j                 → "1.7.21",
        mllib                 → sparkVersion.value,
        parquet_avro          → "1.8.1",
        scalautils            → "2.1.5",
        seqdoop_hadoop_bam    → "7.9.0",
        shapeless             → "2.3.3",
        slf4j                 → "1.3.1",
        spire                 → "0.13.0"
      )
    )
}
