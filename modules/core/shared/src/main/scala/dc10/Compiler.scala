package dc10

import cats.data.StateT
import dc10.file.{SourceFile, VirtualFile}

trait Compiler[F[_], A]:

  type Ctx[E[_], L, X] = StateT[E, L, X]                      // Monad ctx for building programs
  type Err[X]          = Either[List[Error], X]               // Error functor in ctx
  type Dep             = A                                    // Code library dependency alias
  type Γ               = (Set[Dep], List[A])                  // Code level log alias
  type Δ               = (Set[Dep], List[SourceFile[F, A]])   // File level log alias

  extension [X] (ast: Ctx[Err, Γ, X])
    @scala.annotation.targetName("compileCode")
    def compile: Err[F[A]]

  extension [X] (ast: Ctx[Err, Δ, X])
    @scala.annotation.targetName("compileFile")
    def compile: Err[List[SourceFile[F, A]]]

  extension [V] (res: Err[F[A]])
    def string(using R: Renderer[F, V, A]): String

  extension [V] (res: Err[F[A]])
    def stringOrError(using R: Renderer[F, V, A]): Err[String]

  extension [V] (res: Err[List[SourceFile[F, A]]])
    def virtualFile(using R: Renderer[F, V, A]): Either[List[Error], List[VirtualFile]]

  extension (ctx: Γ)
    @scala.annotation.targetName("depΓ")
    def dep(d: Dep): Err[Γ]
    def ext(a: A): Err[Γ]
    def namecheck(a: A): Err[A]

  extension (ctx: Δ)
    @scala.annotation.targetName("depΔ")
    def dep(d: Dep): Err[Δ]
    def ext(s: SourceFile[F, A]): Err[Δ]
    def namecheck(s: SourceFile[F, A]): Err[SourceFile[F, A]]

object Compiler:

  def impl[F[_], A](using F: Format[F]): Compiler[F, A] =
    new Compiler[F, A]:

      extension [X] (ast: Ctx[Err, Γ, X])
        @scala.annotation.targetName("compileCode")
        def compile: Err[F[A]] =
          for
            s <- ast.runEmptyS
            r <- F.output(s._2)
          yield r
  
      extension [X] (ast: Ctx[Err, Δ, X])
        @scala.annotation.targetName("compileFile")
        def compile: Err[List[SourceFile[F, A]]] =
          ast.runEmptyS.map(_._2)

      extension [V] (res: Err[F[A]])
        def string(using R: Renderer[F, V, A]): String =
          res.fold(R.renderErrors, R.render)

      extension [V] (res: Err[F[A]])
        def stringOrError(using R: Renderer[F, V, A]): Err[String] =
          res.map(R.render)

      extension [V] (res: Err[List[SourceFile[F, A]]])
        def virtualFile(using R: Renderer[F, V, A]): Err[List[VirtualFile]] =
          for
            fds <- res
          yield fds.map(f =>
            VirtualFile(f.path, R.render(f.contents))
          )

      extension (ctx: Γ)
        @scala.annotation.targetName("depΓ")
        def dep(d: Dep): Err[Γ] =
          Right((ctx._1 + d, ctx._2))
        def ext(a: A): Err[Γ] =
          namecheck(a).map(stmt =>
           ctx.copy(_2 = ctx._2 :+ stmt)
        )
        def namecheck(a: A): Err[A] =
          // TODO
          Right(a)

      extension (ctx: Δ)
        @scala.annotation.targetName("depΔ")
        def dep(d: Dep): Err[Δ] =
          Right((ctx._1 + d, ctx._2))
        def ext(s: SourceFile[F, A]): Err[Δ] =
          namecheck(s).map(stmt => (ctx._1, ctx._2 :+ stmt))
        def namecheck(s: SourceFile[F, A]): Err[SourceFile[F, A]] =
          // TODO
          Right(s)