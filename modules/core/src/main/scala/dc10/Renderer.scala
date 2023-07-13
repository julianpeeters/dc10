package dc10

trait Renderer[V, A]:
  def render(input: List[A]): String
  def version: V
