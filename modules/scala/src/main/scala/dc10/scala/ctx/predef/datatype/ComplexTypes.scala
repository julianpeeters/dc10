package dc10.scala.ctx.predef.datatype

import cats.data.StateT
import cats.implicits.*
import dc10.scala.ast.Symbol.{CaseClass, Term}
import dc10.scala.ast.Symbol.Term.TypeLevel.__
import dc10.scala.ast.Symbol.Term.ValueLevel
import dc10.scala.ast.Statement
import dc10.scala.error.CompileError
import dc10.scala.ErrorF
import dc10.scala.ctx.ext
import org.tpolecat.sourcepos.SourcePos
import dc10.scala.ast.Statement.Expr

trait ComplexTypes[F[_]]:
  @scala.annotation.targetName("caseClass1")
  def CASECLASS[T, A](name: String, fields: F[Expr[Term.ValueLevel.Var.UserDefinedValue, A]])(using sp: SourcePos): F[(Expr[Term.TypeLevel, T], Expr[ValueLevel, A => T])]
  def LIST: F[Expr[Term.TypeLevel, List[__]]]
  def List[A]: F[Expr[ValueLevel, List[A] => List[A]]]
  extension [A] (list: F[Expr[ValueLevel, List[A] => List[A]]])
    @scala.annotation.targetName("appVL")
    def apply(args: F[Expr[ValueLevel, A]]*): F[Expr[ValueLevel, List[A]]]

object ComplexTypes:

  trait Mixins extends ComplexTypes[[A] =>> StateT[ErrorF, List[Statement], A]]:
 
    @scala.annotation.targetName("caseClass1")
    def CASECLASS[T, A](
      name: String,
      fields: StateT[ErrorF, List[Statement], Expr[Term.ValueLevel.Var.UserDefinedValue,A]]
    )(
      using
        sp: SourcePos
    ): StateT[ErrorF, List[Statement], (Expr[Term.TypeLevel, T], Expr[ValueLevel, A => T])] =
      for
        (fields, a) <- StateT.liftF(fields.runEmpty)
        fs <- StateT.liftF(
          fields.traverse(field => field match
            case d@Statement.CaseClassDef(_,_) => Left(???)
            case d@Statement.ObjectDef(_,_,_)  => Left(???)
            case d@Statement.PackageDef(_,_)   => Left(???)
            case d@Statement.ValDef(_,_)       => Right[List[CompileError], Statement.ValDef](d)
            case d@Statement.Expr.BuiltInType(_)      => Left(???)
            case d@Statement.Expr.BuiltInValue(_)      => Left(???)
            case d@Statement.Expr.UserType(_)      => Left(???)
            case d@Statement.Expr.UserValue(_)      => Left(???)
          )
        )
        c <- StateT.pure(CaseClass[T](name, fs))
        f <- StateT.pure(Expr.BuiltInValue(Term.ValueLevel.Lam1(a.value, Term.ValueLevel.AppCtor1[T, A](c.tpe, a.value))))
        v <- StateT.pure(Expr.UserValue[A => T](
          Term.ValueLevel.Var.UserDefinedValue(name, Term.TypeLevel.App2(Term.TypeLevel.Var.Function1Type, a.value.tpe, c.tpe), Some(f.value))))
        d <- StateT.pure(Statement.CaseClassDef(c, 0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield (Expr.UserType(c.tpe), v)

    def LIST: StateT[ErrorF, List[Statement], Expr[Term.TypeLevel, List[__]]] =
      StateT.pure(Expr.BuiltInType(Term.TypeLevel.Var.ListType))
      
    def List[A]: StateT[ErrorF, List[Statement], Expr[ValueLevel, List[A] => List[A]]] =
      StateT.pure(Expr.BuiltInValue(Term.ValueLevel.Var.ListCtor[A]()))
    
    extension [A] (list: StateT[ErrorF, List[Statement], Expr[ValueLevel, List[A] => List[A]]])
      @scala.annotation.targetName("appVL")
      def apply(args: StateT[ErrorF, List[Statement], Expr[ValueLevel, A]]*): StateT[ErrorF, List[Statement], Expr[ValueLevel, List[A]]] =
        for
          l <- list
          a <- args.toList.sequence
        yield Expr.BuiltInValue(Term.ValueLevel.AppVargs[A, List[A]](l.value, a.map(arg => arg.value)*))
