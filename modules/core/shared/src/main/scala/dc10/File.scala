package dc10

import java.nio.file.Path

case class File[+A](path: Path, contents: List[A])
object File:

  extension [A] (file: File[A])
    def addParent(path: Path): File[A] =
      file.copy(path = path.resolve(file.path))

case class VirtualFile(path: Path, contents: String)