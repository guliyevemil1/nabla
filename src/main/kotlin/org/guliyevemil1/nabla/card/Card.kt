package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.Base

import org.guliyevemil1.nabla.Zero as NablaZero
import org.guliyevemil1.nabla.One as NablaOne
import org.guliyevemil1.nabla.X as NablaX
import org.guliyevemil1.nabla.ExpX as NablaExpX
import org.guliyevemil1.nabla.SinX as NablaSinX
import org.guliyevemil1.nabla.CosX as NablaCosX
import org.guliyevemil1.nabla.Multiply

sealed interface Card

sealed class BaseCard(val b: Base) : Card

val Start: List<Card> = listOf(
    One,
    X,
    X2,
)

object Zero : BaseCard(NablaZero)
object One : BaseCard(NablaOne)
object X : BaseCard(NablaX)
object X2 : BaseCard(Multiply(NablaX, NablaX))
object ExpX : BaseCard(NablaExpX)
object SinX : BaseCard(NablaSinX)
object CosX : BaseCard(NablaCosX)

sealed interface Operator : Card

sealed interface BinaryOperator : Card
