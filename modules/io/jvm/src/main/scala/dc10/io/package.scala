package dc10.io

import cats.effect.Concurrent
import cats.syntax.all.given
import dc10.{Compiler, File, Renderer}
import fs2.io.file.{Files, Path}

extension [F[_]: Concurrent: Files, C, D, E](
  res: Either[List[E], List[File[C]]]
)(using C: Compiler[C, D, E])
  def toFile[V](using R: Renderer[V, E, C]): F[List[Path]] =
    C.toVirtualFile(res)
      .foldMapM(e => e.traverse(s => FileWriter[F].writeFile(s)))