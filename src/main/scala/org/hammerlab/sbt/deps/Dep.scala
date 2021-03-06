package org.hammerlab.sbt.deps

import org.hammerlab.sbt.deps.VersionOps._
import sbt.impl.GroupArtifactID
import sbt.{ ExclusionRule, ModuleID }

case class Dep(group: Group,
               artifact: Artifact,
               crossVersion: CrossVersion,
               configurations: Configurations = Configurations.default,
               excludes: Seq[GroupArtifact] = Nil,
               forceSnapshot: Boolean = false,
               version: Option[String] = None) {

  def snapshot: Dep = copy(forceSnapshot = true)

  def tests: Dep = copy(configurations = Configuration.Test)
  def testtest: Dep = copy(configurations = Configuration.TestTest)
  def provided: Dep = copy(configurations = Configuration.Provided)

  def groupArtifact: GroupArtifact =
    GroupArtifact(
      group,
      artifact,
      crossVersion
    )

  def toModuleIDs(crossVersionFn: (CrossVersion, String) ⇒ String): Seq[ModuleID] =
    version match {
      case Some(version) ⇒
        configurations map {
          case Configuration(scope, classifier) ⇒
            val id =
              ModuleID(
                organization = group.value,
                name = artifact.value,
                revision = version.maybeForceSnapshot(forceSnapshot),
                configurations =
                  scope match {
                    case Scope.Compile ⇒ None
                    case _ ⇒ Some(scope.toString)
                  },
                crossVersion = crossVersion,
                exclusions =
                  this
                    .excludes
                    .map {
                      case GroupArtifact(
                        Group(group),
                        Artifact(artifact),
                        crossVersion
                      ) ⇒
                        ExclusionRule(
                          group,
                          crossVersionFn(crossVersion, artifact)
                        )
                    }
              )

            classifier match {
              case Classifier.Default ⇒ id
              case _ ⇒ id.classifier(classifier.toString)
            }
        }
      case None ⇒
        throw VersionNotSetException(this)
    }

  def withVersion(implicit versionsMap: VersionsMap): Dep =
    version
      .map(_ ⇒ this)
      .orElse(
        versionsMap
          .get(groupArtifact)
          .map(
            version ⇒
              copy(
                version =
                  this
                    .version
                    .orElse(
                      Some(version)
                    )
              )
          )
      )
      .getOrElse {
        throw GroupArtifactNotFound(
          group,
          artifact,
          crossVersion,
          versionsMap
        )
      }

  def -(excludes: Dep*): Dep =
    copy(
      excludes = this.excludes ++ excludes.map(_.groupArtifact)
    )

  def %(configurations: Configuration*): Dep =
    copy(configurations = configurations)

  def +(configurations: Configuration*): Dep =
    copy(configurations = this.configurations ++ configurations)

  def %(version: String): Dep =
    this.copy(version = Some(version))

  def ^(version: String): Dep =
    this.copy(version = Some(version))
}

object Dep {
  implicit def fromSBT(ga: GroupArtifactID): Dep = ga: GroupArtifact
}

case class GroupArtifactNotFound(group: Group,
                                 artifact: Artifact,
                                 crossVersion: CrossVersion,
                                 versionsMap: VersionsMap)
  extends IllegalArgumentException(
    s"$group:$artifact:$crossVersion\n$versionsMap"
  )

case class VersionNotSetException(dep: Dep)
  extends IllegalArgumentException(s"$dep")
