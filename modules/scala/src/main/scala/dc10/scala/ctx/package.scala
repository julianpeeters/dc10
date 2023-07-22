package dc10.scala.ctx

import dc10.compiler.Compiler
import dc10.scala.ast.Definition.Statement
import dc10.schema.FileDef

type ErrorF[A] = Either[List[Compiler.Error], A]

extension (ctx: List[Statement])
  def ext(s: Statement): ErrorF[List[Statement]] =
    namecheck(s).map(ctx :+ _)
  def namecheck(s: Statement): ErrorF[Statement] =
    // TODO
    Right(s)

extension (ctx: List[FileDef[List[Statement]]])
  def ext(s: FileDef[List[Statement]]): ErrorF[List[FileDef[List[Statement]]]] =
    namecheck(s).map(ctx :+ _)
  def namecheck(s: FileDef[List[Statement]]): ErrorF[FileDef[List[Statement]]] =
    // TODO
    Right(s)