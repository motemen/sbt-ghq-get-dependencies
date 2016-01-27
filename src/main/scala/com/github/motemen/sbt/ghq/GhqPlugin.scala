package com.github.motemen.sbt.ghq

import sbt._
import sbt.Keys._

import org.apache.ivy.core.module.id.ModuleRevisionId

import scala.collection.JavaConversions._
import scala.xml.parsing.ConstructingParser

object GhqPlugin extends AutoPlugin {
  lazy val ghqGetDependencies = TaskKey[Unit](
    "ghq-get-dependencies",
    """Download dependencies using "ghq get"."""
  )

  lazy val ghqFilterModule = SettingKey[ModuleID => Boolean](
    "ghq-filter-module",
    "A function to filter which modules are to be downloaded"
  )

  lazy val settings = Seq(
    ghqGetDependencies <<= (ghqFilterModule, ivyPaths, libraryDependencies, streams) map {
      (filterModule, ivyPaths, libraryDependencies, s) =>
        new IvyCache(ivyPaths.ivyHome).withDefaultCache(None, s.log) {
          cache =>
            libraryDependencies.filter(filterModule).foreach {
              m =>
                GhqAction.downloadModule(cache, m, s.log)
            }
        }
    },

    ghqFilterModule := {
      (m: ModuleID) => true
    }
  )

  override def requires = sbt.plugins.IvyPlugin
  override def trigger = allRequirements
  override def projectSettings = settings
}
