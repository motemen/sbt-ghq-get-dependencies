package com.github.motemen.sbt.ghq

import sbt._

trait GhqKeys {
  val ghqGetDependencies = TaskKey[Unit](
    "ghq-get-dependencies",
    """Download dependencies using "ghq get"."""
  )

  val ghqFilterModule = SettingKey[ModuleID => Boolean](
    "ghq-filter-module",
    "A function to filter which modules are to be downloaded"
  )
}
