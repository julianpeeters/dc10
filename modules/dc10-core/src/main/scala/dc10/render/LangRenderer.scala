package dc10.render

import dc10.schema.{Package, Type, Value}
import dc10.schema.define.{CaseClassDef, PackageDef, Statement, UnsafeExpr, ValDef}

trait LangRenderer[V]:
  def render(pkg: Package): String
  def render(stmt: Statement): String
  def render(tpe: Type): String
  def render(value: Value): String
  def version: V
  
object LangRenderer:
  given LangRenderer["scala-3.3.0"] =
    new LangRenderer["scala-3.3.0"]:    
      def render(pkg: Package): String =
        s"package ${pkg.getPath.toString}\n\n${pkg}"
      def render(stmt: Statement): String = stmt match
        case CaseClassDef(cls) =>
          s"case class ${cls.nme}(${cls.fields.map(render).mkString})"
        case PackageDef(pkg) =>
          s"package ${render(pkg)}\n\n"
        case ValDef(value) =>
          s"val ${render(value)}: ${render(value.tpe)}"
        case UnsafeExpr(value) =>
          render(value)
      def render(tpe: Type): String =
        tpe.nme
      def render(value: Value): String =
        value.nme
      def version: "scala-3.3.0" =
        "scala-3.3.0"
    