package dc10.scala.version

import dc10.renderer.Renderer
import dc10.scala.ast.Binding
import dc10.scala.ast.Binding.{Package, Term}
import dc10.scala.ast.Binding.Package.{Basic, Empty}
import dc10.scala.ast.Definition.Statement
import dc10.scala.ast.Definition.Statement.{RecordDef, PackageDef, ValDef}

given `3.3.0`: Renderer["scala-3.3.0", List[Statement]] =
  new Renderer["scala-3.3.0", List[Statement]]:

    def render(input: List[Statement]): String = input.map(stmt => stmt match
      case d@RecordDef(_, _) =>
        s"case class ${d.caseclass.nme}(${render(d.caseclass.fields).mkString})"
      case d@Statement.ObjectDef(_, _, _) =>
        ???
      case d@PackageDef(_, _) =>
        d.pkg match
          case Basic(nme, nst) => s"package ${nme}\n\n"
          case Empty(ms) => render(ms)
      case d@ValDef(_, _) =>
        d.value match
          case Term.ValueLevel.Var.UserDefinedValue(n, t, mi) =>
            mi.fold(
              s"val ${n}: ${renderType(t)}"
            )(
              i =>
                s"val ${n}: ${renderType(t)} = ${renderValue(i)}"
            )
    ).mkString("\n")

    def renderPackage(pkg: Package): String = pkg match
      case Basic(nme, nst) => s".${nme}${renderPackage(nst.pkg)}"
      case Empty(ms) => render(ms)

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

    override def version: "scala-3.3.0" =
      "scala-3.3.0"