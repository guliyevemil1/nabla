package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.Expr

import org.guliyevemil1.nabla.Zero as NablaZero
import org.guliyevemil1.nabla.One as NablaOne
import org.guliyevemil1.nabla.X as NablaX
import org.guliyevemil1.nabla.ExpX as NablaExpX
import org.guliyevemil1.nabla.SinX as NablaSinX
import org.guliyevemil1.nabla.CosX as NablaCosX
import org.guliyevemil1.nabla.Multiply
import org.guliyevemil1.nabla.Pow

sealed interface Card

sealed class BaseCard(val b: Expr<Any?>) : Card

val Start: List<Card> = listOf(
    One,
    X,
    X2,
)

object Zero : BaseCard(NablaZero)
object One : BaseCard(NablaOne)
object X : BaseCard(NablaX)
object X2 : BaseCard(Pow(NablaX, 2))
object ExpX : BaseCard(NablaExpX)
object SinX : BaseCard(NablaSinX)
object CosX : BaseCard(NablaCosX)

sealed interface AllOperator : Card

object Nabla : AllOperator
object Nabla2 : AllOperator

sealed interface Operator : Card

object DDx : Operator
object Integral : Operator

sealed interface BinaryOperator : Card

class Times : BinaryOperator
class Over : BinaryOperator

sealed interface SingleOperator : Card

object Lim0 : SingleOperator
object LimInf : SingleOperator
object LimNegInf : SingleOperator
object LimSupremum : SingleOperator
object LimInfimum : SingleOperator
object Sqrt : SingleOperator
object Log : SingleOperator
object Inverse : SingleOperator
