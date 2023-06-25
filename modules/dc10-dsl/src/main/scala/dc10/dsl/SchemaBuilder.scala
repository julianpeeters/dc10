package dc10.dsl

import cats.data.StateT
import dc10.compile.Compiler
import dc10.schema.Binding
import dc10.schema.Binding.{CaseClass, Type, Value}
import dc10.schema.definition.Statement
import dc10.schema.definition.Statement.{CaseClassDef, ValDef}
import org.tpolecat.sourcepos.SourcePos
import cats.Show

trait SchemaBuilder[F[_]]:
  def CASECLASS[T, A](nme: T, flds: F[A])(using  sh: Show[T], sp: SourcePos): F[(Type[T], A)]
  def BOOLEAN: F[Type[Boolean]]
  def INT: F[Type[Int]]
  def STRING: F[Type[String]]
  def VAL[T](nme: String, tpe: F[Type[T]])(using sp: SourcePos): F[Value[T]]
  def VAL[T](nme: String, tpe: F[Type[T]], impl: F[Value[T]])(using sp: SourcePos): F[Value[T]]
  given litB: Conversion[Boolean, F[Value[Boolean]]]
  given litS: Conversion[String, F[Value[String]]]
  given litZ: Conversion[Int, F[Value[Int]]]
  given refV[T]: Conversion[Value[T], F[Value[T]]]

object SchemaBuilder:

  type Γ = List[Statement[Binding]]
  
  extension (ctx: Γ)
    def ext(s: Statement[Binding]): Compiler.ErrorF[Γ] =
      namecheck(s).map(ctx :+ _)
    def namecheck(s: Statement[Binding]): Compiler.ErrorF[Statement[Binding]] =
      // TODO
      Right(s)
  
  def dsl: SchemaBuilder[[A] =>> StateT[Compiler.ErrorF, Γ, A]] =
    new SchemaBuilder[[A] =>> StateT[Compiler.ErrorF, Γ, A]]:
      
      def CASECLASS[T, A](
        nme: T,
        flds: StateT[Compiler.ErrorF, Γ, A]
      )(using sh: Show[T], sp: SourcePos): StateT[Compiler.ErrorF, Γ, (Type[T], A)] =
        for
          (fs, a) <- StateT.liftF[Compiler.ErrorF, Γ, (Γ, A)](flds.runEmpty)
          // TODO parameterize
          c <- StateT.liftF[Compiler.ErrorF, Γ, CaseClass[T]](CaseClass[T](nme, fs))
          d <- StateT.pure[Compiler.ErrorF, Γ, CaseClassDef](CaseClassDef(c, 0)(sp))
          _ <- StateT.modifyF[Compiler.ErrorF, Γ](ctx => ctx.ext(d))
        yield (
          Type(sh.show(nme), None),
          /* TODO */ a
        )

      def BOOLEAN: StateT[Compiler.ErrorF, Γ, Type[Boolean]] =
        StateT.pure[Compiler.ErrorF, Γ, Type[Boolean]](Type.boolean)

      def INT: StateT[Compiler.ErrorF, Γ, Type[Int]] =
        StateT.pure[Compiler.ErrorF, Γ, Type[Int]](Type.int)

      def STRING: StateT[Compiler.ErrorF, Γ, Type[String]] =
        StateT.pure[Compiler.ErrorF, Γ, Type[String]](Type.string)

      def VAL[T](
        nme: String, 
        tpe: StateT[Compiler.ErrorF, Γ, Type[T]], 
      )(using sp: SourcePos): StateT[Compiler.ErrorF, Γ, Value[T]] =
        for
          v <- StateT.liftF[Compiler.ErrorF, Γ, Value[T]](Value(nme, tpe.runEmptyA))
          d <- StateT.pure[Compiler.ErrorF, Γ, ValDef](ValDef(v, 0)(sp))
          _ <- StateT.modifyF[Compiler.ErrorF, Γ](ctx => ctx.ext(d))
        yield v

      def VAL[T](
        nme: String, 
        tpe: StateT[Compiler.ErrorF, Γ, Type[T]], 
        impl: StateT[Compiler.ErrorF, Γ, Value[T]]
      )(using sp: SourcePos): StateT[Compiler.ErrorF, Γ, Value[T]] =
        for
          v <- StateT.liftF[Compiler.ErrorF, Γ, Value[T]](Value[T](nme, tpe.runEmptyA, impl.runEmptyA))
          d <- StateT.pure[Compiler.ErrorF, Γ, ValDef](ValDef(v, 0)(sp))
          _ <- StateT.modifyF[Compiler.ErrorF, Γ](ctx => ctx.ext(d))
        yield v

      given litB: Conversion[Boolean, StateT[Compiler.ErrorF, Γ, Value[Boolean]]] =
        b => StateT.pure(Value.boolean(b))

      given litS: Conversion[String, StateT[Compiler.ErrorF, Γ, Value[String]]] =
        s => StateT.pure(Value.string(s))

      given litZ: Conversion[Int, StateT[Compiler.ErrorF, Γ, Value[Int]]] =
        z => StateT.pure(Value.int(z))

      given refV[T]: Conversion[Value[T], StateT[Compiler.ErrorF, Γ, Value[T]]] =
        v => StateT.pure(v)