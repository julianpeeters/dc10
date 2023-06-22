package dc10.render

import dc10.schema.{Package, Type, Value}
import dc10.schema.define.{CaseClassDef, PackageDef, Statement, UnsafeExpr, ValDef}

trait LangRenderer[V]:
  def toString(pkg: Package): String
  def toString(stmt: Statement): String
  def toString(tpe: Type): String
  def toString(value: Value): String
  def version: V
  
object LangRenderer:
  given LangRenderer["scala-3.3.0"] =
    new LangRenderer["scala-3.3.0"]:        
      def toString(pkg: Package): String =
        s"package ${pkg.getPath.toString}\n\n${pkg}"
      def toString(stmt: Statement): String = stmt match
        case CaseClassDef(cls) =>
          s"case class ${cls.nme}(${cls.fields.map(toString).mkString})"
        case PackageDef(pkg) =>
          s"package ${toString(pkg)}\n\n"
        case ValDef(value) =>
          s"val ${toString(value)}: ${toString(value.tpe)}"
        case UnsafeExpr(value) =>
          toString(value)
      def toString(tpe: Type): String =
        tpe.nme
      def toString(value: Value): String =
        value.nme
      def version: "scala-3.3.0" =
        "scala-3.3.0"
    