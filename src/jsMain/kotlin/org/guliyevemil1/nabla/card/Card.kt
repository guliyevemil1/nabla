package org.guliyevemil1.nabla.card

interface NablaCard : Card {
    fun render(): String
}

sealed interface BaseCard : NablaCard

object CardZero : BaseCard {
    override fun render(): String = """0"""
}

object CardOne : BaseCard {
    override fun render(): String = """1"""
}

object CardX : BaseCard {
    override fun render(): String = """x"""
}

object CardX2 : BaseCard {
    override fun render(): String = """x^2"""
}

object ExpX : BaseCard {
    override fun render(): String = """e^x"""
}

object SinX : BaseCard {
    override fun render(): String = """\sin x"""
}

object CosX : BaseCard {
    override fun render(): String = """\cos x"""
}

sealed interface AllOperator : NablaCard

object Nabla : AllOperator {
    override fun render(): String = """\nabla"""
}

object Nabla2 : AllOperator {
    override fun render(): String = """\triangle"""
}

sealed interface Operator : NablaCard

object DDx : Operator {
    override fun render(): String = """\frac{d}{dx}"""
}

object Integrate : Operator {
    override fun render(): String = """\int"""
}

sealed interface BinaryOperator : NablaCard

object Times : BinaryOperator {
    override fun render(): String = """\times"""
}

object Over : BinaryOperator {
    override fun render(): String = """\div"""
}

object Lim0 : Operator {
    override fun render(): String = """\displaystyle\lim_{x \to 0}"""
}

object LimInf : Operator {
    override fun render(): String = """\displaystyle\lim_{x \to \infty}"""
}

object LimNegInf : Operator {
    override fun render(): String = """\displaystyle\lim_{x \to -\infty}"""
}

object LimSupremum : Operator {
    override fun render(): String = """\displaystyle\limsup_{x \to 0}"""
}

object LimInfimum : Operator {
    override fun render(): String = """\displaystyle\liminf_{x \to 0}"""
}

object Sqrt : Operator {
    override fun render(): String = """\sqrt{}"""
}

object Log : Operator {
    override fun render(): String = """\log"""
}

object Inverse : Operator {
    override fun render(): String = """f^{-1}"""
}

class NablaDeck : Deck {

    override val cards: List<NablaCard> = buildList {
        repeat(times = 2) { add(CardZero) }
        repeat(times = 2) { add(CardOne) }
        repeat(times = 8) { add(CardX) }
        repeat(times = 3) { add(CardX2) }
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
}
