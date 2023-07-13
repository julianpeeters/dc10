package dc10.io

import cats.{FlatMap, Traverse}
import cats.effect.Concurrent
import cats.implicits.*
import dc10.{Compiler, Config}
import dc10.io.FileWriter
import fs2.io.file.{Files, Path}

extension [E[_]: FlatMap: Traverse, V, A](res: E[List[A]])
  def toFile[F[_]: Concurrent: Files](
    using
      C: Compiler[E, A],
      D: Config[V],
  ): F[E[List[Path]]] =
    FlatMap[E].flatMap(res)(C.generate)
      .traverse(vfs => vfs.traverse(vf => FileWriter[F].writeFile(vf)))
