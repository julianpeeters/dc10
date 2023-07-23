package dc10.scala.ctx.predef.namespace

import cats.data.StateT
import dc10.scala.ast.Definition.Statement
import dc10.scala.ast.Definition.Statement.PackageDef
import dc10.scala.ctx.ErrorF
import dc10.schema.FileDef
import org.tpolecat.sourcepos.SourcePos

trait Packages[F[_], G[_]]:
  def PACKAGE[A](nme: String, files: G[A])(using sp: SourcePos): F[A]

object Packages:

  trait Mixins extends Packages[
    [A] =>> StateT[ErrorF, List[PackageDef], A],
    [A] =>> StateT[ErrorF, List[FileDef[List[Statement]]], A],
  ]:
    def PACKAGE[A](nme: String, files: StateT[ErrorF, List[FileDef[List[Statement]]], A])(using sp: SourcePos): StateT[ErrorF, List[PackageDef], A] =
      ???