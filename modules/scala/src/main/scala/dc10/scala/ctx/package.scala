package dc10.scala.ctx

import dc10.compiler.Compiler
import dc10.schema.FileDef
import dc10.scala.ast.Definition.Statement

type ErrorF[A] = Either[List[Compiler.Error], A]

extension (ctx: List[Statement])
  def ext(s: Statement): ErrorF[List[Statement]] =
    namecheck(s).map(ctx :+ _)
  def namecheck(s: Statement): ErrorF[Statement] =
    // TODO
    Right(s)

extension (ctx: List[FileDef[Statement]])
  def ext(s: FileDef[Statement]): ErrorF[List[FileDef[Statement]]] =
    namecheck(s).map(ctx :+ _)
  def namecheck(s: FileDef[Statement]): ErrorF[FileDef[Statement]] =
    // TODO
    Right(s)