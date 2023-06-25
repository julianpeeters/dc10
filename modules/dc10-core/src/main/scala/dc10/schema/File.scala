package dc10.schema

import dc10.schema.definition.Statement
import java.nio.file.Path

sealed abstract class File:
  def path: Path
  def contents: List[Statement]

object File:
  def apply(p: Path, c: List[Statement]): File =
    new File:
      def path: Path = p
      def contents: List[Statement] = c