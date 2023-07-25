package dc10.scala.file

import dc10.scala.ast.Statement
import java.nio.file.Path

case class ScalaFile(path: Path, contents: List[Statement])  
