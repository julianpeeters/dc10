package dc10.scala.predef.namespace

import cats.implicits.*
import cats.data.StateT
import dc10.scala.ast.{Statement, ScalaFile, Symbol}
import dc10.scala.ctx.ext
import dc10.scala.error.ErrorF
import org.tpolecat.sourcepos.SourcePos
import java.nio.file.Path

trait Packages[F[_]]:
  def PACKAGE[A](nme: String, files: F[A])(using sp: SourcePos): F[A]

object Packages:

  trait Mixins extends Packages[
    [A] =>> StateT[ErrorF, List[ScalaFile], A],
  ]:
    def PACKAGE[A](nme: String, files: StateT[ErrorF, List[ScalaFile], A])(using sp: SourcePos): StateT[ErrorF, List[ScalaFile], A] =
      for
        (ms, a) <- StateT.liftF[ErrorF, List[ScalaFile], (List[ScalaFile], A)](files.runEmpty)
        ss = ms.map(s => s.copy(
          path = Path.of(nme).resolve(s.path),
          contents = List[Statement](Statement.PackageDef(0, Symbol.Package.Basic(nme, Statement.PackageDef(0, Symbol.Package.Empty(s.contents)))))
        ))
        _ <- ss.traverse(d => StateT.modifyF[ErrorF, List[ScalaFile]](ctx => ctx.ext(d)))
      yield a
