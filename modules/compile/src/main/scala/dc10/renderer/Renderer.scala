package dc10.compiler

trait Renderer[V, A]:
  def render(input: List[A]): String
  def version: V
