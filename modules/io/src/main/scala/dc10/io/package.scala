package dc10.io

import cats.effect.Concurrent
import cats.implicits.*
import dc10.compiler.{CodeGenerator, Config}
import dc10.schema.FileSchema
import fs2.io.file.{Files, Path}

extension [F[_]: Concurrent: Files, A](res: F[List[FileSchema[A]]])
  def toFile[V](
    using
      C: CodeGenerator[A],
      D: Config[V],
  ): F[List[Path]] =
    Concurrent[F].map(res)(C.generate)
      .flatMap(vfs => vfs.traverse(vf => FileWriter[F].writeFile(vf)))