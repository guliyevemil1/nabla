package org.guliyevemil1.nabla

sealed interface Base<out T : Expr<T>> : Expr<T>

object X : Base<X>

object CosX : Base<CosX>

object SinX : Base<SinX>

object ExpX : Base<ExpX>
