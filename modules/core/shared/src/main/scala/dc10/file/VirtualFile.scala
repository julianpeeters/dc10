package dc10.file

import fs2.io.file.Path

case class VirtualFile(path: Path, contents: String)