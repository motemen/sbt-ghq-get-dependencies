package com.github.motemen.sbt.ghq

import sbt._

import org.apache.ivy.core.cache.DefaultRepositoryCacheManager
import org.apache.ivy.core.module.id.ModuleRevisionId

import scala.util.{Failure, Success, Try}
import scala.xml.parsing.ConstructingParser
import scala.collection.JavaConversions._

object GhqAction {
  def downloadModule(cache: DefaultRepositoryCacheManager, mod: ModuleID, ivyScala: Option[IvyScala], log: Logger): Unit = {
    val modRevId = ModuleRevisionId.newInstance(
      mod.organization,
      CrossVersion(mod, ivyScala).fold(mod.name)(_(mod.name)),
      mod.revision,
      mod.extraAttributes
    )

    val path = cache.getIvyFileInCache(modRevId).getPath
    extractScmUrlFromFile(s"$path.original") match {
      case Success(Some(url)) =>
        log.info(s"$modRevId :: $url")
        downloadUrl(url)

      case Success(None) =>
        log.info(s"$modRevId :: (not found)")

      case Failure(e) =>
        log.warn(s"$modRevId :: $e")
    }
  }

  def extractScmUrlFromFile(path: String): Try[Option[String]] = {
    Try {
      val doc = ConstructingParser.fromSource(io.Source.fromFile(path), preserveWS = false).document()
      val pat = "^scm:(?:git|hg|svn):(.+)$".r

      (doc \ "scm" \ "connection").text match {
        case pat(url) => Some(url)
        case _ => None
      }
    }
  }

  def downloadUrl(url: String): Unit = {
    Process(Seq("ghq", "get", url)).!
  }
}
