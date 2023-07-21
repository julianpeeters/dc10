package dc10.scala.version

import cats.implicits.*
import dc10.renderer.Renderer
import dc10.ast.Definition.Statement
import dc10.ast.Definition.Statement.{RecordDef, PackageDef, ValDef}
import dc10.ast.Binding
import dc10.ast.Binding.{Package, Term}
import dc10.ast.Binding.Package.Basic
import dc10.ast.Binding.Package.Empty
import dc10.compiler.Compiler

given `3.3.0`: Renderer[Compiler.ErrorF, "scala-3.3.0", Statement] =
  new Renderer[Compiler.ErrorF, "scala-3.3.0", Statement]:

    def render(input: List[Statement]): Compiler.ErrorF[String] =
      def recurse(in: List[Statement]): Compiler.ErrorF[List[String]] =
        in.foldM(List.empty[String])((acc, stmt) => stmt match
          case d@RecordDef(_, _) =>
            for
              fields <- recurse(d.caseclass.fields).map(_.mkString)
            yield acc :+ s"case class ${d.caseclass.nme}(${fields})"
          case d@Statement.ObjectDef(_, _, _) =>
            ???
          case d@PackageDef(_, _) =>
            d.pkg match
              case Basic(nme, nst) => Right(acc :+ s"package ${nme}\n\n")
              case Empty(ms) => recurse(ms)
          case d@ValDef(_, _) =>
            d.value match
              case Term.ValueLevel.Var.UserDefinedValue(n, t, mi) =>
                mi.fold(
                  for
                    tpe <- renderType(t)
                  yield acc :+ s"val ${n}: ${tpe}"
                )(
                  i =>
                    for
                      tpe <- renderType(t)
                      value <- renderValue(i)
                    yield acc :+ s"val ${n}: ${tpe} = ${value}"
              )
        )
      recurse(input).map(_.mkString("\n"))
      

    def renderPackage(pkg: Package): Compiler.ErrorF[String] = pkg match
      case Basic(nme, nst) =>
        for
          rest <- renderPackage(nst.pkg)
        yield s".${nme}${rest}"
      case Empty(ms) => render(ms)

    def renderType[T](tpe: Term.TypeLevel[T]): Compiler.ErrorF[String] =
      tpe match
        // application
        case Term.TypeLevel.App1(tfun, targ) =>
          for
            f <- renderType(tfun)
            a <- renderType(targ)
          yield s"${f}[${a}]"
        case Term.TypeLevel.App2(tfun, ta, tb) =>
          for
            f <- renderType(tfun)
            a <- renderType(ta)
            b <- renderType(tb)
          yield s"${a} ${f} ${b}"
        // primitive
        case Term.TypeLevel.Var.BooleanType => Right("Boolean")
        case Term.TypeLevel.Var.IntType => Right("Int")
        case Term.TypeLevel.Var.StringType => Right("String")
        // complex
        case Term.TypeLevel.Var.Function1Type => Right("=>")
        case Term.TypeLevel.Var.ListType => Right("List")
        case Term.TypeLevel.Var.UserDefinedType(s, i) => Right(s)

    def renderValue[T](value: Term.ValueLevel[T]): Compiler.ErrorF[String] =
      value match 
        // application
        case Term.ValueLevel.App1(f, a) =>
          for
            fun <- renderValue(f)
            arg <- renderValue(a)
          yield s"${fun}(${arg})"
        case Term.ValueLevel.AppCtor1(t, a) =>
          for
            targ <- renderType(t)
            arg <- renderValue(a)
          yield s"${targ}(${arg})"
        case Term.ValueLevel.AppVargs(f, as*) =>
          for
            fun <- renderValue(f)
            vargs <- as.traverse(a => renderValue(a)).map(_.mkString(", "))
          yield s"${fun}(${vargs})"
        // function
        case Term.ValueLevel.Lam1(a, b) =>
          for
            arg <- renderValue(a)
            res <- renderValue(b)
          yield s"${arg} => ${res}"
        // primitive
        case Term.ValueLevel.Var.BooleanLiteral(b) => Right(s"$b")
        case Term.ValueLevel.Var.IntLiteral(i) => Right(s"$i")
        case Term.ValueLevel.Var.StringLiteral(s) => Right(s"\"${s}\"")
        // complex
        case Term.ValueLevel.Var.ListCtor() => Right(s"List")
        case Term.ValueLevel.Var.UserDefinedValue(s, t, i) => Right(s)

    override def version: Compiler.ErrorF["scala-3.3.0"] =
      Right("scala-3.3.0")

