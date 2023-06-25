package dc10.render

import dc10.schema.Binding
import dc10.schema.Binding.{Package, Type, Value}
import dc10.schema.definition.Statement

trait LangRenderer[V]:
  def render(pkg: Package): String
  def render(stmt: Statement[Binding]): String
  def render(tpe: Type[Any]): String
  def render(value: Value[Any]): String
  def version: V
  
object LangRenderer:
  given LangRenderer["scala-3.3.0"] =
    new LangRenderer["scala-3.3.0"]:    
      def render(pkg: Package): String =
        s"package ${pkg.getPath.toString}\n\n${pkg}"
      def render(stmt: Statement[Binding]): String = stmt match
        case d: Statement.CaseClassDef =>
          s"case class ${d.caseclass.nme}(${d.caseclass.fields.map(f => render(f)).mkString})"
        case d: Statement.PackageDef =>
          s"package ${render(d.pkg)}\n\n"
        case d: Statement.ValDef =>
          d.value.impl.fold(
            s"val ${d.value.nme}: ${d.value.tpe.nme}"
          )(
            rhs =>
              s"val ${d.value.nme}: ${d.value.tpe.nme} = ${rhs.nme}"
          )
        case e: Statement.UnsafeExpr =>
          render(e)

      def render(tpe: Type[Any]): String =
        tpe.nme
      def render(value: Value[Any]): String =
        value.nme
      def version: "scala-3.3.0" =
        "scala-3.3.0"
    