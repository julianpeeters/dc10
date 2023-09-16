package dc10.scala.ast

import java.nio.file.Path

case class ScalaFile(path: Path, contents: List[Statement])  
