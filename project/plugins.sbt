// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.1")

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

addSbtPlugin("net.litola" % "play-sass" % "0.2.0")
