package dc10.io

import cats.effect.Concurrent
import cats.syntax.all.*
import dc10.VirtualFile
import fs2.{Stream, text}
import fs2.io.file.{Files, Path}

trait FileWriter[F[_]]:
  def writeFile(vf: VirtualFile): F[Path]

object FileWriter:
  def apply[F[_]: Files: Concurrent]: FileWriter[F] =
    new FileWriter[F]:
      def writeFile(vf: VirtualFile): F[Path] =
        for
          d <- Concurrent[F].pure(Path.fromNioPath(vf.path.getParent()))
          _ <- Files[F].createDirectories(d)
          p <- Concurrent[F].pure(Path.fromNioPath(vf.path))
          _ <- Stream(vf.contents)
              .through(text.utf8.encode)
              .through(Files[F].writeAll(p))
              .compile
              .drain
        yield p