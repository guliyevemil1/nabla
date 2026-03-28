package org.guliyevemil1.nabla.math

interface Matcher<T> : Expr<T>

object ConstantMatcher : Matcher<Nothing> {
    override val isConstant: Boolean = true
    override fun matches(other: Expr<*>): Boolean = other.isConstant
    override fun render(): String = "c"
    override fun toLisp(): String = "c"
}
