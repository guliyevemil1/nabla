package org.guliyevemil1.nabla.card

sealed interface Card

sealed interface BaseCard : Card

val Start: List<Card> = listOf(
    One,
    X,
    X2,
)

object Zero : BaseCard
object One : BaseCard
object X : BaseCard
object X2 : BaseCard
object ExpX : BaseCard
object SinX : BaseCard
object CosX : BaseCard

sealed interface AllOperator : Card

object Nabla : AllOperator
object Nabla2 : AllOperator

sealed interface Operator : Card

object DDx : Operator
object Integrate : Operator

sealed interface BinaryOperator : Card

object Times : BinaryOperator
object Over : BinaryOperator

object Lim0 : Operator
object LimInf : Operator
object LimNegInf : Operator
object LimSupremum : Operator
object LimInfimum : Operator
object Sqrt : Operator
object Log : Operator
object Inverse : Operator

val deck = buildList<Card> {
    repeat(times = 2) { add(Zero) }
    repeat(times = 2) { add(One) }
    repeat(times = 8) { add(X) }
    repeat(times = 3) { add(X2) }
    repeat(times = 4) { add(SinX) }
    repeat(times = 4) { add(CosX) }
    repeat(times = 4) { add(ExpX) }

    repeat(times = 10) { add(Nabla) }
    repeat(times = 2) { add(Nabla2) }

    repeat(times = 8) { add(Integrate) }
    repeat(times = 8) { add(DDx) }

    repeat(times = 2) { add(Inverse) }
    repeat(times = 2) { add(Sqrt) }
    repeat(times = 3) { add(Log) }

    repeat(times = 5) { add(Times) }
    repeat(times = 5) { add(Over) }

    repeat(times = 2) { add(Lim0) }
    repeat(times = 2) { add(LimInf) }
    repeat(times = 2) { add(LimNegInf) }
    add(LimSupremum)
    add(LimInfimum)
}
