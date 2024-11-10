package dc10.io

import cats.effect.Concurrent
import cats.syntax.all.given
import dc10.{Compiler, File, Renderer}
import fs2.io.file.{Files, Path}

extension [F[_]: Concurrent: Files, C, D, E, V](
  res: Either[List[E], List[File[C]]]
)(using C: Compiler[C, D, E])
  def file(using R: Renderer[C, E, V]): F[List[Path]] =
    C.virtualFile(res)
      .foldMapM(l => l.traverse(f => FileWriter[F].writeFile(f)))