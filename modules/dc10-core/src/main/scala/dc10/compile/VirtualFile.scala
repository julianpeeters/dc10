package dc10.compile

import java.nio.file.Path

trait VirtualFile:
  def path: Path
  def contents: String

object VirtualFile:
  def apply(p: Path, c: String): VirtualFile =
    new VirtualFile:
      def path = p
      def contents = c