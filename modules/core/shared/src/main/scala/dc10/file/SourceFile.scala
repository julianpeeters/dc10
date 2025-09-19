package dc10.file

import fs2.io.file.Path

case class SourceFile[F[_], A](path: Path, contents: F[A])