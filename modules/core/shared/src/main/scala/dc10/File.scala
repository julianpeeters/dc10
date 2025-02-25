package dc10

import java.nio.file.Path

case class Source[+A](path: Path, contents: List[A])
object Source:

  extension [A] (file: Source[A])
    def addParent(path: Path): Source[A] =
      file.copy(path = path.resolve(file.path))

case class VirtualFile(path: Path, contents: String)