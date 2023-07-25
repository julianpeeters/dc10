package dc10.io

import cats.Traverse
import cats.effect.Concurrent
import cats.implicits.*
import dc10.compiler.Compiler
import fs2.io.file.{Files, Path}

extension [
  F[_]: Concurrent: Files,
  G[_]: Traverse,
  A
](res: G[List[Compiler.VirtualFile]])
  def toFile[V]: F[G[List[Path]]] =
    Traverse[G].traverse(res)(vfs =>
      vfs.traverse(vf => FileWriter[F].writeFile(vf))
    )