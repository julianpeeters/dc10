package dc10.scala.ctx.predef.datatype

import cats.data.StateT
import cats.implicits.*
import dc10.Compiler
import dc10.Compiler.ErrorF
import dc10.scala.ast.Binding
import dc10.scala.ast.Binding.{CaseClass, Term}
import dc10.scala.ast.Binding.Term.TypeLevel.__
import dc10.scala.ast.Definition.Statement
import dc10.scala.ctx.ext
import org.tpolecat.sourcepos.SourcePos

trait ComplexTypes[F[_]]:
  @scala.annotation.targetName("caseClass1")
  def CASECLASS[T, A](name: String, fields: F[Term.ValueLevel.Var.UserDefinedValue[A]])(using sp: SourcePos): F[(Term.TypeLevel[T], Term.ValueLevel[A => T])]
  def LIST: F[Term.TypeLevel[List[__]]]
  def List[A]: F[Term.ValueLevel[List[A] => List[A]]]
  extension [A] (list: F[Term.ValueLevel[List[A] => List[A]]])
    @scala.annotation.targetName("appVL")
    def apply(args: F[Term.ValueLevel[A]]*): F[Term.ValueLevel[List[A]]]

object ComplexTypes:

  trait Mixins extends ComplexTypes[[A] =>> StateT[ErrorF, List[Statement], A]]:
 
    @scala.annotation.targetName("caseClass1")
    def CASECLASS[T, A](
      name: String,
      fields: StateT[ErrorF, List[Statement], Term.ValueLevel.Var.UserDefinedValue[A]]
    )(
      using
        sp: SourcePos
    ): StateT[ErrorF, List[Statement], (Term.TypeLevel[T], Term.ValueLevel[A => T])] =
      for
        (fields, a) <- StateT.liftF(fields.runEmpty)
        fs <- StateT.liftF(
          fields.traverse(field => field match
            case d@Statement.CaseClassDef(_,_) => Left(???)
            case d@Statement.ObjectDef(_,_,_)  => Left(???)
            case d@Statement.PackageDef(_,_)   => Left(???)
            case d@Statement.ValDef(_,_)       => Right[List[Compiler.Error], Statement.ValDef](d)
          )
        )
        c <- StateT.pure(CaseClass[T, A](name, fs))
        f <- StateT.pure(Term.ValueLevel.Lam1(a, Term.ValueLevel.AppCtor1(c.tpe, a)))
        v <- StateT.pure(Term.ValueLevel.Var.UserDefinedValue(name, Term.TypeLevel.App2(Term.TypeLevel.Var.Function1Type, a.tpe, c.tpe), Some(f)))
        d <- StateT.pure(Statement.CaseClassDef(c, 0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield (c.tpe, v)

    def LIST: StateT[ErrorF, List[Statement], Term.TypeLevel[List[__]]] =
      StateT.pure(Term.TypeLevel.Var.ListType)
      
    def List[A]: StateT[ErrorF, List[Statement], Term.ValueLevel[List[A] => List[A]]] =
      StateT.pure(Term.ValueLevel.Var.ListCtor[A]())
    
    extension [A] (list: StateT[ErrorF, List[Statement], Term.ValueLevel[List[A] => List[A]]])
      @scala.annotation.targetName("appVL")
      def apply(args: StateT[ErrorF, List[Statement], Term.ValueLevel[A]]*): StateT[ErrorF, List[Statement], Term.ValueLevel[List[A]]] =
        for
          l <- list
          a <- args.toList.sequence
        yield Term.ValueLevel.AppVargs[A, List[A]](l, a*)
