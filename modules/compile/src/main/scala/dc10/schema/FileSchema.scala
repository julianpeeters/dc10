package dc10.schema

import java.nio.file.Path

case class FileSchema[A](path: Path, contents: List[A])