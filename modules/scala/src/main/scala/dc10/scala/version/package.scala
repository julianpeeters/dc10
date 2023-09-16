package dc10.scala.version

import dc10.compile.Renderer
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.{CaseClassDef, PackageDef, TypeExpr, ValDef, ValueExpr}
import dc10.scala.ast.Symbol.Package.{Basic, Empty}
import dc10.scala.ast.Symbol.Term
import dc10.scala.ast.Symbol.Term.ValueLevel.{App1, AppCtor1, AppVargs, Lam1}
import dc10.scala.ast.Symbol.Term.ValueLevel.Var.{BooleanLiteral, IntLiteral, StringLiteral, ListCtor, UserDefinedValue}
import dc10.scala.error.CompileError

given `3.3.0`: Renderer["scala-3.3.0", CompileError, List[Statement]] =
  new Renderer["scala-3.3.0", CompileError, List[Statement]]:

    def render(input: List[Statement]): String = input.map(stmt => stmt match
      case d@CaseClassDef(_, _) =>
        s"case class ${d.caseclass.nme}(${render(d.caseclass.fields).mkString})"
      case d@Statement.ObjectDef(_, _, _) =>
        ???
      case d@PackageDef(_, _) =>
        d.pkg match
          case Basic(nme, nst) => s"package ${nme}\n\n"
          case Empty(ms) => render(ms)
      case d@ValDef(_, _) =>
        d.value.tail.value match
          case UserDefinedValue(nme, tpe, impl) =>  impl.fold(
              s"val ${nme}: ${renderType(tpe)}"
            )(
              i =>
                s"val ${nme}: ${renderType(tpe)} = ${renderValue(i.tail.value)}"
            )
          case Term.ValueLevel.App1(_, _) => ""
          case Term.ValueLevel.AppCtor1(_, _) => ""
          case Term.ValueLevel.AppVargs(_, _) => ""
          case Term.ValueLevel.Lam1(_, _) => ""
          case Term.ValueLevel.Var.BooleanLiteral(_) => ""
          case Term.ValueLevel.Var.IntLiteral(_) => ""
          case Term.ValueLevel.AppVargs(_, _*) => ""
          case Term.ValueLevel.Var.StringLiteral(_) => ""
          case Term.ValueLevel.Var.ListCtor() => ""

      case TypeExpr(t) => 
        renderType(t)
      case ValueExpr(v) => 
        renderValue(v.tail.value)
    ).mkString("\n")

    def renderErrors(errors: List[CompileError]): String =
      errors.map(_.toString()).mkString("\n")

    override def version: "scala-3.3.0" =
      "scala-3.3.0"

    private def renderType[T](tpe: Term.TypeLevel[T]): String =
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

    private def renderValue[T, X](value: Term.ValueLevel[T, X]): String =
      value match 
        // application
        case Term.ValueLevel.App1(f, a) => s"${renderValue(f.tail.value)}(${renderValue(a.tail.value)})"
        case Term.ValueLevel.AppCtor1(t, a) => s"${renderType(t)}(${renderValue(a.tail.value)})"
        case Term.ValueLevel.AppVargs(f, as*) => s"${renderValue(f.tail.value)}(${as.map(a => renderValue(a.tail.value)).mkString(", ")})"
        // function
        case Term.ValueLevel.Lam1(a, b) => s"${renderValue(a.tail.value)} => ${renderValue(b.tail.value)}"
        // primitive
        case Term.ValueLevel.Var.BooleanLiteral(b) => s"$b"
        case Term.ValueLevel.Var.IntLiteral(i) => s"$i"
        case Term.ValueLevel.Var.StringLiteral(s) => s"\"${s}\""
        // complex
        case Term.ValueLevel.Var.ListCtor() => s"List"
        case Term.ValueLevel.Var.UserDefinedValue(s, t, i) => s
