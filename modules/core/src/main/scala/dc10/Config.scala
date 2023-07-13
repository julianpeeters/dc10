package dc10

import java.nio.file.Path

trait Config[V]:
  def target: Path

object Config:
  
  given Config["scala-3.3.0"] =
    new Config["scala-3.3.0"]:
      def target: Path = Path.of("")