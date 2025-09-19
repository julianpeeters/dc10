package dc10.io

import cats.effect.Concurrent
import cats.syntax.all.given
import dc10.{Compiler, Error, Renderer}
import dc10.file.SourceFile
import fs2.io.file.{Files, Path}

extension [
  F[_]: Concurrent: Files,
  G[_], // Output unit, e.g., NonEmptyList
  A,    // Code representation
](
  res: Either[List[Error], List[SourceFile[G, A]]]
)(using C: Compiler[G, A])
  def file[V](using R: Renderer[G, A, V]): F[List[Path]] =
    C.virtualFile(res).fold(
      es => Concurrent[F].raiseError(new Throwable(es.mkString("\n"))),
      l => l.traverse(f => FileWriter[F].writeFile(f))
    )