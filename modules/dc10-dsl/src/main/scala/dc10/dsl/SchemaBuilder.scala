package dc10.dsl

import cats.data.StateT
import dc10.compile.Compiler
import dc10.schema.{CaseClass, Type, Value}
import dc10.schema.definition.Statement
import dc10.schema.definition.Statement.{CaseClassDef, ValDef}
import org.tpolecat.sourcepos.SourcePos

trait SchemaBuilder[F[_]]:
  type Type
  type Value
  def CASECLASS[A](nme: String, flds: F[A])(using sp: SourcePos): F[(Type, Value)]
  def STRING: F[Type]
  def VAL(nme: String, tpe: F[Type])(using sp: SourcePos): F[Value]
  def VAL(nme: String, tpe: F[Type], impl: F[Value])(using sp: SourcePos): F[Value]
  given litS: Conversion[String, F[Value]]
  given refV: Conversion[Value, F[Value]]

object SchemaBuilder:

  type Γ = List[Statement]
  
  extension (ctx: Γ)
    def ext(s: Statement): Compiler.ErrorF[Γ] =
      namecheck(s).map(ctx :+ _)
    def namecheck(s: Statement): Compiler.ErrorF[Statement] =
      // TODO
      Right(s)
  
  def dsl: SchemaBuilder[[A] =>> StateT[Compiler.ErrorF, Γ, A]] =
    new SchemaBuilder[[A] =>> StateT[Compiler.ErrorF, Γ, A]]:

      type Type = dc10.schema.Type

      type Value = dc10.schema.Value

      def CASECLASS[A](
        nme: String,
        flds: StateT[Compiler.ErrorF, Γ, A]
      )(using sp: SourcePos): StateT[Compiler.ErrorF, Γ, (Type, Value)] =
        for
          (fs, a) <- StateT.liftF[Compiler.ErrorF, Γ, (Γ, A)](flds.runEmpty)
          c <- StateT.liftF[Compiler.ErrorF, Γ, CaseClass](CaseClass(nme, fs))
          d <- StateT.pure[Compiler.ErrorF, Γ, CaseClassDef](CaseClassDef(c, 0)(sp))
          _ <- StateT.modifyF[Compiler.ErrorF, Γ](ctx => ctx.ext(d))
        yield (
          Type(nme, None),
          /* TODO */ Value.string("hello world")
        )

      def STRING: StateT[Compiler.ErrorF, Γ, Type] =
        StateT.pure[Compiler.ErrorF, Γ, Type](Type.string)

      def VAL(
        nme: String, 
        tpe: StateT[Compiler.ErrorF, Γ, Type], 
      )(using sp: SourcePos): StateT[Compiler.ErrorF, Γ, Value] =
        for
          v <- StateT.liftF[Compiler.ErrorF, Γ, Value](Value(nme, tpe.runEmptyA))
          d <- StateT.pure[Compiler.ErrorF, Γ, ValDef](ValDef(v, 0)(sp))
          _ <- StateT.modifyF[Compiler.ErrorF, Γ](ctx => ctx.ext(d))
        yield v

      def VAL(
        nme: String, 
        tpe: StateT[Compiler.ErrorF, Γ, Type], 
        impl: StateT[Compiler.ErrorF, Γ, Value]
      )(using sp: SourcePos): StateT[Compiler.ErrorF, Γ, Value] =
        for
          v <- StateT.liftF[Compiler.ErrorF, Γ, Value](Value(nme, tpe.runEmptyA, impl.runEmptyA))
          d <- StateT.pure[Compiler.ErrorF, Γ, ValDef](ValDef(v, 0)(sp))
          _ <- StateT.modifyF[Compiler.ErrorF, Γ](ctx => ctx.ext(d))
        yield v

      given litS: Conversion[String, StateT[Compiler.ErrorF, Γ, Value]] =
        s => StateT.pure(Value.string(s))

      given refV: Conversion[Value, StateT[Compiler.ErrorF, Γ, Value]] =
        v => StateT.pure(v)