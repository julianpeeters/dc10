package dc10.compile

import java.nio.file.Path

trait VirtualFile:
  def path: Path
  def contents: String

object VirtualFile:
  def apply(p: Path, s: String): VirtualFile =
    new VirtualFile:
      def path = p
      def contents = s