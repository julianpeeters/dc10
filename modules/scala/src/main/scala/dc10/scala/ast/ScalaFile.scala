package dc10.scala.ast

import java.nio.file.Path

case class ScalaFile(path: Path, contents: List[Statement])
object ScalaFile:

  extension (file: ScalaFile)
    def addParent(path: Path): ScalaFile =
      file.copy(path = path.resolve(file.path))

