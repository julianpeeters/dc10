package dc10.scala.ctx

import dc10.Compiler
import dc10.scala.ast.Definition.Statement

type ErrorF[A] = Either[List[Compiler.Error], A]

extension (ctx: List[Statement])
  def ext(s: Statement): ErrorF[List[Statement]] =
    namecheck(s).map(ctx :+ _)
  def namecheck(s: Statement): ErrorF[Statement] =
    // TODO
    Right(s)