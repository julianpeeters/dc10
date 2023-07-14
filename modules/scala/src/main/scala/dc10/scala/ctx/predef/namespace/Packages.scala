package dc10.scala.ctx.predef.namespace

import cats.data.StateT
// import cats.implicits.*
import dc10.compiler.Compiler
import dc10.compiler.Compiler.ErrorF
// import dc10.scala.ast.Binding
// import dc10.scala.ast.Binding.{CaseClass, Term}
// import dc10.scala.ast.Binding.Term.TypeLevel.__
import dc10.scala.ast.Definition.Statement
// import dc10.scala.ctx.ext
import org.tpolecat.sourcepos.SourcePos
// import dc10.scala.ast.Definition.ScalaFile
import dc10.scala.ast.Definition.Statement.PackageDef
import dc10.schema.FileDef

trait Packages[F[_], G[_]]:
  def PACKAGE[A](nme: String, files: G[A])(using sp: SourcePos): F[A]

object Packages:

  trait Mixins extends Packages[
    [A] =>> StateT[ErrorF, List[PackageDef], A],
    [A] =>> StateT[ErrorF, List[FileDef[Statement]], A],
  ]:
    def PACKAGE[A](nme: String, files: StateT[ErrorF, List[FileDef[Statement]], A])(using sp: SourcePos): StateT[ErrorF, List[PackageDef], A] =
      ???