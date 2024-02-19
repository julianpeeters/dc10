package dc10.io

import cats.effect.Concurrent
import cats.Foldable
import cats.syntax.all.*
import dc10.compile.{Compiler, Renderer}
import fs2.io.file.{Files, Path}

extension [
  F[_]: Concurrent: Files,
  G[_]: Foldable,
  H[_],
  E,
  A,
  B
](res: G[H[B]])(using C: Compiler[G, H, E, A, B])
  def toFile[V](using R: Renderer[V, E, H[A]]): F[List[Path]] =
    C.toVirtualFile(res)
      .foldMapM(e => e.traverse(s => FileWriter[F].writeFile(s)))