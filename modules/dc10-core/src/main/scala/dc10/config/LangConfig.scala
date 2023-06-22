package dc10.config

import java.nio.file.Path

trait LangConfig[V]:
  def target: Path

object LangConfig:
  
  given LangConfig["scala-3.3.0"] =
    new LangConfig["scala-3.3.0"]:
      def target: Path = Path.of("")