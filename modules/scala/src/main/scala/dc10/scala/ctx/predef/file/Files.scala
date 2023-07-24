package dc10.scala.ctx.predef.file

import cats.data.StateT
import dc10.scala.ast.Binding
import dc10.scala.ast.Binding.Package
import dc10.scala.ctx.ErrorF
import dc10.scala.ctx.ext
import dc10.schema.FileSchema
import java.nio.file.Path
import org.tpolecat.sourcepos.SourcePos
import dc10.scala.ast.Statement

trait Files[F[_], G[_]]:
  def FILE[A](nme: String, statements: F[A])(using sp: SourcePos): G[A]
  
object Files:

  trait Mixins extends Files[
    [A] =>> StateT[ErrorF, List[Statement], A],
    [A] =>> StateT[ErrorF, List[FileSchema[List[Statement]]], A]
    ]:

    def FILE[A](
      nme: String,
      statements: StateT[ErrorF, List[Statement], A]
    )(using sp: SourcePos): StateT[ErrorF, List[FileSchema[List[Statement]]], A] =
      for
        (ms, a) <- StateT.liftF[ErrorF, List[FileSchema[List[Statement]]], (List[Statement], A)](statements.runEmpty)
        n <- StateT.pure[ErrorF, List[FileSchema[List[Statement]]], Path](Path.of(nme))
        p <- StateT.pure[ErrorF, List[FileSchema[List[Statement]]], Statement.PackageDef](
          Statement.PackageDef(0, Package.Empty(ms)))
        d <- StateT.pure[ErrorF, List[FileSchema[List[Statement]]], FileSchema[List[Statement]]](FileSchema[List[Statement]](n, List(p)))
        _ <- StateT.modifyF[ErrorF, List[FileSchema[List[Statement]]]](ctx => ctx.ext(d))
      yield a