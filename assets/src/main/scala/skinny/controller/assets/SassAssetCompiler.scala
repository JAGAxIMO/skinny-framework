package skinny.controller.assets

import skinny.assets._

/**
  * Sass (indented-sass)
  */
object SassAssetCompiler extends AssetCompiler {
  private[this] val compiler = SassCompiler

  def dir(basePath: String)                 = s"${basePath}/sass"
  def extension                             = "sass"
  def compile(path: String, source: String) = compiler.compileIndented(path, source)
}
