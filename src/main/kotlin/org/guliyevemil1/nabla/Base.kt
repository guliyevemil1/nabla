package org.guliyevemil1.nabla

sealed interface Base

object X : Expr<Base>

object CosX : Expr<Base>

object SinX : Expr<Base>

object ExpX : Expr<Base>
