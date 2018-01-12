# org.hammerlab.sbt:root

```scala
addSbtPlugin("org.hammerlab.sbt" % "root" % "4.0.0")
```

[The `Root` plugin](src/main/scala/org/hammerlab/sbt/plugin/Root.scala) provides the `rootProject` helper for aggregating a project's modules and no-op'ing various settings/tasks that should not operate directly on the root wrapper-module:

```scala
lazy val    base = rootProject(module1, module2)
lazy val module1 = project.settings(…)
lazy val module2 = project.settings(…)
```

Additionally, `build` is a wrapper for applying settings `in ThisBuild`, which will apply to all subprojects in a project:

```scala
build(
  v"1.2.3",                          // version-setting shorthand
  github("my-org", "repo-name")  // fills in scmInfo setting, e.g. for corresponding POM field
)

// Each of these will inherit the version and scmInfo settings defined above
lazy val subproject1 = project.settings(…)
lazy val subproject2 = project.settings(…)
```
