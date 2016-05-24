package skinny.task.generator

import skinny.{ DBSettings, SkinnyEnv }
import scalikejdbc._
import scalikejdbc.metadata.Table
import skinny.nlp.Inflector

/**
 * Reverse Model All generator.
 */
object ReverseScaffoldAllGenerator extends ReverseScaffoldAllGenerator {
}

trait ReverseScaffoldAllGenerator extends CodeGenerator {

  protected def showUsage = {
    showSkinnyGenerator()
    println("""  Usage: sbt "task/run generate:reverse-model-all [env]""")
    println("")
  }

  protected def initializeDB(skinnyEnv: Option[String]): Unit = {
    System.setProperty(SkinnyEnv.PropertyKey, skinnyEnv.getOrElse(SkinnyEnv.Development))
    DBSettings.initialize()
  }

  def run(templateType: String, args: List[String], skinnyEnv: Option[String]) {

    initializeDB(skinnyEnv)

    val tables: Seq[Table] = DB.getAllTableNames.filter(_.toLowerCase != "schema_version").flatMap { tableName =>
      DB.getTable(tableName)
    }
    val self = this
    val generator = new ReverseScaffoldGenerator {
      override def cachedTables = tables
      override def useAutoConstruct = true
      override def createAssociationsForForeignKeys = true

      override def sourceDir = self.sourceDir
      override def testSourceDir = self.testSourceDir
      override def resourceDir = self.resourceDir
      override def testResourceDir = self.testResourceDir
      override def webInfDir = self.webInfDir
      override def controllerPackage = self.controllerPackage
      override def controllerPackageDir = self.controllerPackageDir
      override def modelPackage = self.modelPackage
      override def modelPackageDir = self.modelPackageDir
    }
    tables.map { table =>
      val tableName = table.name.toLowerCase
      val className = Inflector.singularize(toClassName(tableName))
      val args: List[String] = List(tableName, Inflector.pluralize(className), className)
      generator.run(templateType, args, SkinnyEnv.get())
    }
  }

}
