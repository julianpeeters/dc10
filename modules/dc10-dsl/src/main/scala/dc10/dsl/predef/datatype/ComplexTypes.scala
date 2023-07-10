package dc10.dsl.predef.datatype

import cats.implicits.*
import cats.data.StateT
import dc10.ast.Binding.{CaseClass, Term}
import dc10.ast.Binding.Term.TypeLevel.__
import dc10.ast.Definition.Statement.CaseClassDef
import dc10.ast.predef.{Application, Function}
import dc10.compile.Compiler.{Γ, ErrorF, ext}
import org.tpolecat.sourcepos.SourcePos

trait ComplexTypes[F[_]]:
  def CASECLASS[T, A](name: String, fields: F[Term.ValueLevel.Var.UserDefinedValue[A]])(using sp: SourcePos): F[(Term.TypeLevel[T], Term.ValueLevel[A => T])]
  def LIST: F[Term.TypeLevel[List[__]]]
  def List[A]: F[Term.ValueLevel[List[A] => List[A]]]
  extension [A] (list: F[Term.ValueLevel[List[A] => List[A]]])
    @scala.annotation.targetName("appVL")
    def apply(args: F[Term.ValueLevel[A]]*): F[Term.ValueLevel[List[A]]]

object ComplexTypes:

  trait Mixins extends ComplexTypes[[A] =>> StateT[ErrorF, Γ, A]]:
 

    def CASECLASS[T, A](
      name: String,
      fields: StateT[ErrorF, Γ, Term.ValueLevel.Var.UserDefinedValue[A]]
    )(
      using
        sp: SourcePos
    ): StateT[ErrorF, Γ, (Term.TypeLevel[T], Term.ValueLevel[A => T])] =
      for
        (fs, a) <- StateT.liftF(fields.runEmpty)
        c <- StateT.liftF(CaseClass[T, A](name, fs))
        b <- StateT.liftF(Application[ErrorF].ctor1Application[T, A](c.tpe, a))
        f <- StateT.liftF(Function[ErrorF].function1Value[A, T](a, b))
        v <- StateT.pure(Term.ValueLevel.Var.UserDefinedValue(name, Term.TypeLevel.App2(Term.TypeLevel.Var.Function1Type, a.tpe, c.tpe), Some(f)))
        d <- StateT.pure(CaseClassDef(c, 0))
        _ <- StateT.modifyF[ErrorF, Γ](ctx => ctx.ext(d))
      yield (c.tpe, v)

    def LIST: StateT[ErrorF, Γ, Term.TypeLevel[List[__]]] =
      StateT.pure(Term.TypeLevel.Var.ListType)
      
    def List[A]: StateT[ErrorF, Γ, Term.ValueLevel[List[A] => List[A]]] =
      StateT.pure(Term.ValueLevel.Var.ListCtor[A]())
    
    extension [A] (list: StateT[ErrorF, Γ, Term.ValueLevel[List[A] => List[A]]])
      @scala.annotation.targetName("appVL")
      def apply(args: StateT[ErrorF, Γ, Term.ValueLevel[A]]*): StateT[ErrorF, Γ, Term.ValueLevel[List[A]]] =
        for
          l <- list
          a <- args.toList.sequence
        yield Term.ValueLevel.AppVargs[A, List[A]](l, a*)
