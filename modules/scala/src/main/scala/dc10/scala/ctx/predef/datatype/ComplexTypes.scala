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
import dc10.scala.ast.Statement.{TypeExpr, ValueExpr}
import dc10.scala.ast.Symbol.Term.ValueLevel.App1
import dc10.scala.ast.Symbol.Term.ValueLevel.AppCtor1
import dc10.scala.ast.Symbol.Term.ValueLevel.AppVargs
import dc10.scala.ast.Symbol.Term.ValueLevel.Lam1
import dc10.scala.ast.Symbol.Term.ValueLevel.Var.BooleanLiteral
import dc10.scala.ast.Symbol.Term.ValueLevel.Var.IntLiteral
import dc10.scala.ast.Symbol.Term.ValueLevel.Var.StringLiteral
import dc10.scala.ast.Symbol.Term.ValueLevel.Var.ListCtor
import dc10.scala.ast.Symbol.Term.ValueLevel.Var.UserDefinedValue

trait ComplexTypes[F[_]]:
  @scala.annotation.targetName("caseClass1")
  def CASECLASS[T, A](name: String, fields: F[ValueExpr[A]])(using sp: SourcePos): F[(TypeExpr[T], ValueExpr[A => T])]
  def LIST: F[TypeExpr[List[__]]]
  def List[A]: F[ValueExpr[List[A] => List[A]]]
  extension [A] (list: F[ValueExpr[List[A] => List[A]]])
    @scala.annotation.targetName("appVL")
    def apply(args: F[ValueExpr[A]]*): F[ValueExpr[List[A]]]

object ComplexTypes:

  trait Mixins extends ComplexTypes[[A] =>> StateT[ErrorF, List[Statement], A]]:
 
    @scala.annotation.targetName("caseClass1")
    def CASECLASS[T, A](
      name: String,
      fields: StateT[ErrorF, List[Statement], ValueExpr[A]]
    )(
      using
        sp: SourcePos
    ): StateT[ErrorF, List[Statement], (TypeExpr[T], ValueExpr[A => T])] =
      for
        (fields, a) <- StateT.liftF(fields.runEmpty)
        fs <- StateT.liftF(
          fields.traverse(field => field match
            case d@Statement.CaseClassDef(_,_) => Left(???)
            case d@Statement.ObjectDef(_,_,_)  => Left(???)
            case d@Statement.PackageDef(_,_)   => Left(???)
            case d@Statement.ValDef(_,_)       => Right[List[CompileError], Statement.ValDef](d)
            case d@Statement.TypeExpr(_)       => Left(???)
            case d@Statement.ValueExpr(_)      => Left(???)
   
          )
        )
        c <- StateT.pure(CaseClass[T](name, fs))
        f <- StateT.pure(ValueExpr(Term.ValueLevel.Lam1(a.value, Term.ValueLevel.AppCtor1[T, A](c.tpe, a.value))))
        v <- StateT.liftF(
          a.value match
            case App1(fun, arg) => Left(???)
            case AppCtor1(tpe, arg) => Left(???)
            case AppVargs(fun, vargs*) => Left(???)
            case Lam1(a, b) => Left(???)
            case BooleanLiteral(b) => Left(???)
            case IntLiteral(i) => Left(???)
            case StringLiteral(s) => Left(???)
            case ListCtor() => Left(???)
            case UserDefinedValue(nme, tpe, impl) =>
              Right[List[CompileError], Statement.ValueExpr[A => T]](
                ValueExpr[A => T](Term.ValueLevel.Var.UserDefinedValue(name, Term.TypeLevel.App2(Term.TypeLevel.Var.Function1Type, tpe, c.tpe), Some(f.value)))
              )
          
          
        )
        d <- StateT.pure(Statement.CaseClassDef(c, 0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield (TypeExpr(c.tpe), v)

    def LIST: StateT[ErrorF, List[Statement], TypeExpr[List[__]]] =
      StateT.pure(TypeExpr(Term.TypeLevel.Var.ListType))
      
    def List[A]: StateT[ErrorF, List[Statement], ValueExpr[List[A] => List[A]]] =
      StateT.pure(ValueExpr(Term.ValueLevel.Var.ListCtor[A]()))
    
    extension [A] (list: StateT[ErrorF, List[Statement], ValueExpr[List[A] => List[A]]])
      @scala.annotation.targetName("appVL")
      def apply(args: StateT[ErrorF, List[Statement], ValueExpr[A]]*): StateT[ErrorF, List[Statement], ValueExpr[List[A]]] =
        for
          l <- list
          a <- args.toList.sequence
        yield ValueExpr(Term.ValueLevel.AppVargs[A, List[A]](l.value, a.map(arg => arg.value)*))
