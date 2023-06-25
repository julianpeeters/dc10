package dc10.schema

import cats.data.Validated
import cats.implicits.*
import dc10.compile.Compiler

sealed abstract class Value:
  def nme: String
  def tpe: Type
  def impl: Option[Value]

object Value:

  def apply(
    n: String,
    t: Type,
    i: Option[Value]
  ): Value =
    new Value:
      def nme: String = s"${n}"
      def tpe: Type = t
      def impl: Option[Value] = i
  
  def apply(
    nme: String,
    tpe: Compiler.ErrorF[Type],
  ): Compiler.ErrorF[Value] =
    (
      Validated.valid(nme),
      Validated.fromEither(tpe)
    ).mapN(
      (n, t) =>
        Value(n, t, None)
    ).toEither

  def apply(
    nme: String,
    tpe: Compiler.ErrorF[Type],
    impl: Compiler.ErrorF[Value]
  ): Compiler.ErrorF[Value] =
    (
      Validated.valid(nme),
      Validated.fromEither(tpe),
      Validated.fromEither(impl)
    ).mapN(
      (n, t, i) =>
        Value(n, t, Some(i))
    ).toEither
    
  def string(s: String): Value =
    Value(s"\"${s}\"", Type.string, None)