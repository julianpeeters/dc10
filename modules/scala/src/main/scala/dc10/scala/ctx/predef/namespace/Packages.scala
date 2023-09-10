package dc10.scala.ctx.predef.namespace

// import cats.*
import cats.implicits.*
import cats.data.StateT
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.PackageDef
import dc10.scala.ast.Symbol
import dc10.scala.ctx.ext
import dc10.scala.ErrorF
import dc10.scala.file.ScalaFile
import org.tpolecat.sourcepos.SourcePos
import java.nio.file.Path

trait Packages[F[_], G[_]]:
  def PACKAGE[A](nme: String, files: G[A])(using sp: SourcePos): G[A]

object Packages:

  trait Mixins extends Packages[
    [A] =>> StateT[ErrorF, List[PackageDef], A],
    [A] =>> StateT[ErrorF, List[ScalaFile], A],
  ]:
    def PACKAGE[A](nme: String, files: StateT[ErrorF, List[ScalaFile], A])(using sp: SourcePos): StateT[ErrorF, List[ScalaFile], A] =
      for
        (ms, a) <- StateT.liftF[ErrorF, List[ScalaFile], (List[ScalaFile], A)](files.runEmpty)
        ss = ms.map(s => s.copy(
          path = Path.of(nme).resolve(s.path),
          contents = List(PackageDef(0, Symbol.Package.Basic(nme, PackageDef(0, Symbol.Package.Empty(s.contents)))))
        ))
        _ <- ss.traverse(d => StateT.modifyF[ErrorF, List[ScalaFile]](ctx => ctx.ext(d)))
      yield a
