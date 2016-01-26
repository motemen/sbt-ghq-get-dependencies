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

  lazy val settings = Seq(
    ghqGetDependencies <<= (ivyPaths, libraryDependencies, streams) map {
      (ivyPaths, libraryDependencies, s) =>
        new IvyCache(ivyPaths.ivyHome).withDefaultCache(None, s.log) {
          cache =>
            libraryDependencies.foreach {
              m =>
                GhqAction.downloadModule(cache, m, s.log)
            }
        }
    }
  )

  override def requires = sbt.plugins.IvyPlugin
  override def trigger = allRequirements
  override def projectSettings = settings
}
