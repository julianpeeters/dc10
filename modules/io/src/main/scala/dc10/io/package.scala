package dc10.io

import cats.{FlatMap, Foldable, Monoid}
import cats.data.StateT
import cats.effect.Concurrent
import cats.implicits.*
import dc10.compiler.{Compiler, Config}
import dc10.schema.FileDef
import fs2.io.file.{Files, Path}

extension [
  F[_]: FlatMap: Foldable,
  G[_]: Concurrent,
  L: Monoid,
  A
] (ast: StateT[F, L, A])
  def compile: G[L] =
    ast.runEmptyS.foldMapA(Concurrent[G].pure)

extension [F[_]: Concurrent: Files, A](res: F[List[FileDef[A]]])
  def toFile[V](
    using
      C: Compiler[A],
      D: Config[V],
  ): F[List[Path]] =
    Concurrent[F].map(res)(C.generate)
      .flatMap(vfs => vfs.traverse(vf => FileWriter[F].writeFile(vf)))