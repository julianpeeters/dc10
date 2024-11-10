package dc10

import cats.data.StateT

trait Compiler[
  C, // Code statement representation
  D, // Lib dependency representation
  E, // Error message representation
]:

  type Ctx[F[_], L, A] = StateT[F, L, A]         // Monadic ctx in which to build up a program
  type Err[A]          = Either[List[E], A]      // Error functor in ctx
  type Γ               = (Set[D], List[C])       // Code level log
  type Δ               = (Set[D], List[File[C]]) // File level log
  
  extension [A] (ast: Ctx[Err, Γ, A])
    @scala.annotation.targetName("compileCode")
    def compile: Err[List[C]]

  extension [A] (ast: Ctx[Err, Δ, A])
    @scala.annotation.targetName("compileFile")
    def compile: Err[List[File[C]]]

  extension [V] (res: Err[List[C]])
    def string(using R: Renderer[C, E, V]): String

  extension [V] (res: Err[List[C]])
    def stringOrError(using R: Renderer[C, E, V]): Err[String]

  extension [V] (res: Err[List[File[C]]])
    def virtualFile(using R: Renderer[C, E, V]): Err[List[VirtualFile]]

  extension (ctx: Γ)
    @scala.annotation.targetName("depΓ")
    def dep(d: D): Err[Γ]
    def ext(s: C): Err[Γ]
    def namecheck(s: C): Err[C]

  extension (ctx: Δ)
    @scala.annotation.targetName("depΔ")
    def dep(d: D): Err[Δ]
    def ext(s: File[C]): Err[Δ]
    def namecheck(s: File[C]): Err[File[C]]

object Compiler:

  def impl[C, D, E]: Compiler[C, D, E] =
    new  Compiler[C, D, E]:

      extension [A] (ast: Ctx[Err, Γ, A])
        @scala.annotation.targetName("compileCode")
        def compile: Err[List[C]] =
          ast.runEmptyS.map(_._2)

      extension [A] (ast: Ctx[Err, Δ, A])
        @scala.annotation.targetName("compileFile")
        def compile: Err[List[File[C]]] =
          ast.runEmptyS.map(_._2)

      extension [V] (res: Err[List[C]])
        def string(
          using R: Renderer[C, E, V]
        ): String =
          res.fold(R.renderErrors, R.render)

      extension [V] (res: Err[List[C]])
        def stringOrError(
          using R: Renderer[C, E, V]
        ): Err[String] =
          res.map(R.render)

      extension [V] (res: Err[List[File[C]]])
        def virtualFile(
          using R: Renderer[C, E, V]
        ): Err[List[VirtualFile]] =
          for
            fds <- res
          yield fds.map(f =>
            VirtualFile(f.path, R.render(f.contents))
          )

      extension (ctx: Γ)
        @scala.annotation.targetName("depΓ")
        def dep(d: D): Err[Γ] =
          Right((ctx._1 + d, ctx._2))
        def ext(s: C): Err[Γ] =
          namecheck(s).map(stmt => (ctx._1, ctx._2 :+ stmt))
        def namecheck(s: C): Err[C] =
          // TODO
          Right(s)

      extension (ctx: Δ)
        @scala.annotation.targetName("depΔ")
        def dep(d: D): Err[Δ] =
          Right((ctx._1 + d, ctx._2))
        def ext(s: File[C]): Err[Δ] =
          namecheck(s).map(stmt => (ctx._1, ctx._2 :+ stmt))
        def namecheck(s: File[C]): Err[File[C]] =
          // TODO
          Right(s)