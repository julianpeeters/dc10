package dc10.renderer

trait Renderer[V, A]:
  def render(input: A): String
  def version: V
