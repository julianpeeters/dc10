package dc10.scala.predef.file

import cats.data.StateT
import dc10.scala.ast.{ScalaFile, Statement, Symbol}
import dc10.scala.ctx.ext
import dc10.scala.error.ErrorF
import java.nio.file.Path
import org.tpolecat.sourcepos.SourcePos

trait Files[F[_], G[_]]:
  def FILE[A](nme: String, statements: F[A])(using sp: SourcePos): G[A]
  
object Files:

  trait Mixins extends Files[
    [A] =>> StateT[ErrorF, List[Statement], A],
    [A] =>> StateT[ErrorF, List[ScalaFile], A]
    ]:

    def FILE[A](
      nme: String,
      statements: StateT[ErrorF, List[Statement], A]
    )(using sp: SourcePos): StateT[ErrorF, List[ScalaFile], A] =
      for
        (ms, a) <- StateT.liftF[ErrorF, List[ScalaFile], (List[Statement], A)](statements.runEmpty)
        n <- StateT.pure[ErrorF, List[ScalaFile], Path](Path.of(nme))
        p <- StateT.pure[ErrorF, List[ScalaFile], Statement.PackageDef](
          Statement.PackageDef(Symbol.Package.Empty(ms), 0))
        d <- StateT.pure[ErrorF, List[ScalaFile], ScalaFile](ScalaFile(n, List(p)))
        _ <- StateT.modifyF[ErrorF, List[ScalaFile]](ctx => ctx.ext(d))
      yield a