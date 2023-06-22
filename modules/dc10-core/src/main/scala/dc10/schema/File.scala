package dc10.schema

import dc10.schema.define.Statement
import java.nio.file.Path

case class File(path: Path, contents: List[Statement])