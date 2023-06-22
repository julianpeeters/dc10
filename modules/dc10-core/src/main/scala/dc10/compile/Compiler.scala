package dc10.compile

import dc10.config.LangConfig
import dc10.render.LangRenderer
import dc10.schema.define.{FileDef, Statement}

trait Compiler[F[_], V, A]:
  def toVirtualFile(res: List[A], cfg: LangConfig[V]): F[List[VirtualFile]]

object Compiler:

  sealed trait Error

  type ResultF[A] = Either[List[Compiler.Error], A]

  given compiler[V] (
    using
      C: LangConfig[V],
      R: LangRenderer[V],
  ): Compiler[ResultF, V, FileDef] =
    new Compiler[ResultF, V, FileDef]:
      def toVirtualFile(
        res: List[FileDef],
        cfg: LangConfig[V]
      ): Either[List[Compiler.Error], List[VirtualFile]] =
        Right(
          res.map(fileDef =>
            VirtualFile(
              fileDef.file.path,
              fileDef.file.contents.map(R.toString).mkString)))

  extension [A] (res: Either[List[Compiler.Error], List[A]])
    def toVirtualFile[V](
      using
        C: Compiler[ResultF, V, A],
        D: LangConfig[V]
    ): Either[List[Compiler.Error], List[VirtualFile]] =
      res.flatMap(r => C.toVirtualFile(r, D))

      