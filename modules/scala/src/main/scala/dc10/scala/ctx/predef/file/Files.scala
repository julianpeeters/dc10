package dc10.scala.ctx.predef.file

import cats.data.StateT
import dc10.scala.ast.Binding
import dc10.scala.ast.Binding.Package
import dc10.scala.ast.Definition.Statement
import dc10.scala.ctx.ext
import dc10.compiler.Compiler
import dc10.compiler.Compiler.ErrorF
import dc10.schema.FileDef
import java.nio.file.Path
import org.tpolecat.sourcepos.SourcePos

trait Files[F[_], G[_]]:
  def FILE[A](nme: String, statements: F[A])(using sp: SourcePos): G[A]
  
object Files:

  trait Mixins extends Files[
    [A] =>> StateT[ErrorF, List[Statement], A],
    [A] =>> StateT[ErrorF, List[FileDef[Statement]], A]
    ]:

    def FILE[A](
      nme: String,
      statements: StateT[ErrorF, List[Statement], A]
    )(using sp: SourcePos): StateT[ErrorF, List[FileDef[Statement]], A] =
      for
        (ms, a) <- StateT.liftF[ErrorF, List[FileDef[Statement]], (List[Statement], A)](statements.runEmpty)
        n <- StateT.pure[ErrorF, List[FileDef[Statement]], Path](Path.of(nme))
        p <- StateT.pure[ErrorF, List[FileDef[Statement]], Statement.PackageDef](
          Statement.PackageDef(0, Package.Empty(ms)))
        // f <- StateT.pure[ErrorF, List[FileDef[Statement]], File](File(n, List(p)))
        d <- StateT.pure[ErrorF, List[FileDef[Statement]], FileDef[Statement]](FileDef[Statement](n, List(p)))
        _ <- StateT.modifyF[ErrorF, List[FileDef[Statement]]](ctx => ctx.ext(d))
      yield a