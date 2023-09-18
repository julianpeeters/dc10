package dc10.scala.predef.datatype

import cats.data.StateT
import cats.Eval
import cats.free.Cofree
import cats.implicits.*
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.{TypeExpr, ValueExpr}
import dc10.scala.ast.Symbol.{CaseClass, Term}
import dc10.scala.ast.Symbol.Term.TypeLevel.__
import dc10.scala.ast.Symbol.Term.ValueLevel.{App1, AppCtor1, AppVargs, Lam1}
import dc10.scala.ast.Symbol.Term.ValueLevel.Var.{BooleanLiteral, IntLiteral, StringLiteral, ListCtor, UserDefinedValue}
import dc10.scala.ctx.ext
import dc10.scala.error.{CompileError, ErrorF, IdentifierStatementExpected, IdentifierSymbolExpected}
import org.tpolecat.sourcepos.SourcePos

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
        (fields, a) <- StateT.liftF[ErrorF, List[Statement], (List[Statement], ValueExpr[A])](fields.runEmpty)
        fs <- StateT.liftF(
          fields.traverse(field => field match
            case d@Statement.CaseClassDef(_,_) => Left(scala.List(IdentifierStatementExpected(d)))
            case d@Statement.ObjectDef(_,_,_)  => Left(scala.List(IdentifierStatementExpected(d)))
            case d@Statement.PackageDef(_,_)   => Left(scala.List(IdentifierStatementExpected(d)))
            case d@Statement.ValDef(_,_)       => Right[List[CompileError], Statement.ValDef](d)
            case d@Statement.TypeExpr(_)       => Left(scala.List(IdentifierStatementExpected(d)))
            case d@Statement.ValueExpr(_)      => Left(scala.List(IdentifierStatementExpected(d)))
          )
        )
        c <- StateT.pure(CaseClass[T](None, name, fs))
        f <- StateT.pure[ErrorF, List[Statement], ValueExpr[A => T]](ValueExpr(
          Cofree((), Eval.now(Term.ValueLevel.Lam1(None, a.value, Cofree((), Eval.now(Term.ValueLevel.AppCtor1(None, c.tpe, a.value))))))))
        v <- StateT.liftF[ErrorF, List[Statement], ValueExpr[A => T]](
          a.value.tail.value match
            case App1(_, _, _)          => Left(scala.List(IdentifierSymbolExpected(a.value.tail.value)))
            case AppCtor1(_, _, _)      => Left(scala.List(IdentifierSymbolExpected(a.value.tail.value)))
            case AppVargs(_, _, vargs*) => Left(scala.List(IdentifierSymbolExpected(a.value.tail.value)))
            case Lam1(_, _, _)          => Left(scala.List(IdentifierSymbolExpected(a.value.tail.value)))
            case BooleanLiteral(_, _)   => Left(scala.List(IdentifierSymbolExpected(a.value.tail.value)))
            case IntLiteral(_, _)       => Left(scala.List(IdentifierSymbolExpected(a.value.tail.value)))
            case StringLiteral(_, _)    => Left(scala.List(IdentifierSymbolExpected(a.value.tail.value)))
            case ListCtor(_)          => Left(scala.List(IdentifierSymbolExpected(a.value.tail.value)))
            case UserDefinedValue(qnt, nme, tpe, impl) => Right[List[CompileError], Statement.ValueExpr[A => T]](ValueExpr[A => T](
              Cofree((), Eval.now(Term.ValueLevel.Var.UserDefinedValue(qnt, name, Cofree((), Eval.now(Term.TypeLevel.App2(None, Cofree((), Eval.now(Term.TypeLevel.Var.Function1Type(None))), tpe, c.tpe))), Some(f.value))))))
        )
        d <- StateT.pure(Statement.CaseClassDef(c, 0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield (TypeExpr(c.tpe), v)

    def LIST: StateT[ErrorF, List[Statement], TypeExpr[List[__]]] =
      StateT.pure(TypeExpr(Cofree((), Eval.now(Term.TypeLevel.Var.ListType(None)))))
      
    def List[A]: StateT[ErrorF, List[Statement], ValueExpr[List[A] => List[A]]] =
      StateT.pure(ValueExpr(Cofree((), Eval.now(Term.ValueLevel.Var.ListCtor(None)))))
    
    extension [A] (list: StateT[ErrorF, List[Statement], ValueExpr[List[A] => List[A]]])
      @scala.annotation.targetName("appVL")
      def apply(args: StateT[ErrorF, List[Statement], ValueExpr[A]]*): StateT[ErrorF, List[Statement], ValueExpr[List[A]]] =
        for
          l <- list
          a <- args.toList.sequence
        yield ValueExpr(Cofree((), Eval.now(Term.ValueLevel.AppVargs(None, l.value, a.map(arg => arg.value)*))))
