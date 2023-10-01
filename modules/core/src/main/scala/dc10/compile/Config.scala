package dc10.compile

import java.nio.file.Path

trait Config[V]:
  def target: Path

object Config:
  
  given Config["scala-3.3.1"] =
    new Config["scala-3.3.1"]:
      def target: Path = Path.of("")