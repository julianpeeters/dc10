package dc10.scala.version

import dc10.compile.Renderer
import dc10.scala.ast.Symbol.Package.{Basic, Empty}
import dc10.scala.ast.Symbol.Term
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.{CaseClassDef, PackageDef, ValDef, Expr}
import dc10.scala.ast.Statement.Expr.{BuiltInType, BuiltInValue, UserType, UserValue}
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
        d.value match
          case UserValue(value) =>
           value.impl.fold(
              s"val ${value.nme}: ${renderExpr(value.tpe)}"
            )(
              i =>
                s"val ${value.nme}: ${renderExpr(value.tpe)} = ${renderExpr(i)}"
            )
      case BuiltInType(t) => 
        renderType(t) 
      case BuiltInValue(v) => 
        renderValue(v) 
      case UserType(t) => 
        renderType(t)
      case UserValue(v) => 
        renderValue(v)
    ).mkString("\n")

    def renderErrors(errors: List[CompileError]): String =
      errors.map(_.toString()).mkString("\n")

    override def version: "scala-3.3.0" =
      "scala-3.3.0"

    private def renderExpr[F[_], T](expr: Expr[F, T]): String =
      expr match
        case BuiltInType(v) =>        
          renderType(v)
        case BuiltInValue(v) =>        
          renderValue(v)
        case UserType(t) =>
          renderType(t)
        case UserValue(v) =>
          renderValue(v)
    private def renderType[T](tpe: Term.TypeLevel[T]): String =
      tpe match
        // application
        case Term.TypeLevel.App1(tfun, targ) => s"${renderExpr(tfun)}[${renderExpr(targ)}]"
        case Term.TypeLevel.App2(tfun, ta, tb) => s"${renderExpr(ta)} ${renderExpr(tfun)} ${renderExpr(tb)}"
        // primitive
        case Term.TypeLevel.Var.BooleanType => "Boolean"
        case Term.TypeLevel.Var.IntType => "Int"
        case Term.TypeLevel.Var.StringType => "String"
        // complex
        case Term.TypeLevel.Var.Function1Type => "=>"
        case Term.TypeLevel.Var.ListType => "List"
        case Term.TypeLevel.Var.UserDefinedType(s, i) => s

    private def renderValue[T](value: Term.ValueLevel[T]): String =
      value match 
        // application
        case Term.ValueLevel.App1(f, a) => s"${renderExpr(f)}(${renderExpr(a)})"
        case Term.ValueLevel.AppCtor1(t, a) => s"${renderExpr(t)}(${renderExpr(a)})"
        case Term.ValueLevel.AppVargs(f, as*) => s"${renderExpr(f)}(${as.map(a => renderExpr(a)).mkString(", ")})"
        // function
        case Term.ValueLevel.Lam1(a, b) => s"${renderExpr(a)} => ${renderExpr(b)}"
        // primitive
        case Term.ValueLevel.Var.BooleanLiteral(b) => s"$b"
        case Term.ValueLevel.Var.IntLiteral(i) => s"$i"
        case Term.ValueLevel.Var.StringLiteral(s) => s"\"${s}\""
        // complex
        case Term.ValueLevel.Var.ListCtor() => s"List"
        case Term.ValueLevel.Var.UserDefinedValue(s, t, i) => s
