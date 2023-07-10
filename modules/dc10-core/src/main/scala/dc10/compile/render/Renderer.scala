package dc10.compile.render

import dc10.ast.Binding
import dc10.ast.Binding.{Package, Term}
import dc10.ast.Binding.Term.ValueLevel.Var.{BooleanLiteral, IntLiteral, StringLiteral}
import dc10.ast.Binding.Term.TypeLevel.Var.Function1Type
import dc10.ast.Definition.Statement
import dc10.ast.Definition.Statement.{CaseClassDef, PackageDef, ValDef}

trait Renderer[V]:
  def renderPackage(pkg: Package): String
  def renderStatement(stmt: Statement[Binding]): String
  def renderType[T](tpe: Term.TypeLevel[T]): String
  def renderValue[T](value: Term.ValueLevel[T]): String
  def version: V
  
object Renderer:

  given Renderer["scala-3.3.0"] =
    new Renderer["scala-3.3.0"]:

      def renderPackage(pkg: Package): String =
        s"package ${pkg.getPath.toString}\n\n${pkg}"
      def renderStatement(stmt: Statement[Binding]): String = stmt match
        case d@CaseClassDef(_, _) =>
          s"case class ${d.caseclass.nme}(${d.caseclass.fields.map(f => renderStatement(f)).mkString})"
        case d@PackageDef(_, _) =>
          s"package ${renderPackage(d.pkg)}\n\n"
        case d@ValDef(_, _) =>
          d.value match
            case Term.ValueLevel.Var.UserDefinedValue(n, t, mi) =>
              mi.fold(
                s"val ${n}: ${renderType(t)}"
              )(
                i =>
                  s"val ${n}: ${renderType(t)} = ${renderValue(i)}"
              )

      def renderType[T](tpe: Term.TypeLevel[T]): String =
        tpe match
          // application
          case Term.TypeLevel.App1(tfun, targ) => s"${renderType(tfun)}[${renderType(targ)}]"
          case Term.TypeLevel.App2(tfun, ta, tb) => s"${renderType(ta)} ${renderType(tfun)} ${renderType(tb)}"
          // primitive
          case Term.TypeLevel.Var.BooleanType => "Boolean"
          case Term.TypeLevel.Var.IntType => "Int"
          case Term.TypeLevel.Var.StringType => "String"
          // complex
          case Term.TypeLevel.Var.Function1Type => "=>"
          case Term.TypeLevel.Var.ListType => "List"
          case Term.TypeLevel.Var.UserDefinedType(s, i) => s
          
      def renderValue[T](value: Term.ValueLevel[T]): String =
        value match 
          // application
          case Term.ValueLevel.App1(f, a) => s"${renderValue(f)}(${renderValue(a)})"
          case Term.ValueLevel.AppCtor1(t, a) => s"${renderType(t)}(${renderValue(a)})"
          case Term.ValueLevel.AppVargs(f, as*) => s"${renderValue(f)}(${as.map(a => renderValue(a)).mkString(", ")})"
          // function
          case Term.ValueLevel.Lam1(a, b) => s"${renderValue(a)} => ${renderValue(b)}"
          // primitive
          case Term.ValueLevel.Var.BooleanLiteral(b) => s"$b"
          case Term.ValueLevel.Var.IntLiteral(i) => s"$i"
          case Term.ValueLevel.Var.StringLiteral(s) => s"\"${s}\""
          // complex
          case Term.ValueLevel.Var.ListCtor() => s"List"
          case Term.ValueLevel.Var.UserDefinedValue(s, t, i) => s

      def version: "scala-3.3.0" =
        "scala-3.3.0"
    