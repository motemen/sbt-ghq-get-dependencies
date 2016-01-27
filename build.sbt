sbtPlugin := true

name := "sbt-ghq-get-dependencies"

version := {
  ("git describe --tags --match v* --dirty=-SNAPSHOT" !!) trim
}

description := """An sbt plugin to clone dependency repositories using "ghq get""""

organization := "com.github.motemen"

licenses += "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")

publishMavenStyle := false

bintrayRepository := "sbt-plugins"

bintrayOrganization := None
