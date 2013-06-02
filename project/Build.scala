import sbt._
import Keys._
import play.Project._
import net.litola.SassPlugin

object ApplicationBuild extends Build {

    val appName         = "lens-pal"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.reactivemongo" %% "play2-reactivemongo" % "0.9"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      SassPlugin.sassSettings:_*
    )

}
