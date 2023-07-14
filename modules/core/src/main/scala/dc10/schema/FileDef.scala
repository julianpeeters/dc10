package dc10.schema

import java.nio.file.Path

case class FileDef[A](path: Path, contents: List[A])