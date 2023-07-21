package dc10.renderer

trait Renderer[F[_], V, A]:
  def render(input: List[A]): F[String]
  def version: F[V]
