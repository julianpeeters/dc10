package dc10.renderer

trait Renderer[V, A]:
  def render(input: List[A]): String
  def version: V
