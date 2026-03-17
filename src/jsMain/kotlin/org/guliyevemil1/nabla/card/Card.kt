package org.guliyevemil1.nabla.card

sealed interface Card {
    fun render(): String
}

sealed interface BaseCard : Card

val Start: List<Card> = listOf(
    One,
    X,
    X2,
)

object Zero : BaseCard {
    override fun render(): String = """0"""
}

object One : BaseCard {
    override fun render(): String = """1"""
}

object X : BaseCard {
    override fun render(): String = """x"""
}

object X2 : BaseCard {
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

sealed interface AllOperator : Card

object Nabla : AllOperator {
    override fun render(): String = TODO()
}

object Nabla2 : AllOperator {
    override fun render(): String = TODO()
}

sealed interface Operator : Card

object DDx : Operator {
    override fun render(): String = """\frac{d}{dx}"""
}

object Integrate : Operator {
    override fun render(): String = """\int"""
}

sealed interface BinaryOperator : Card

object Times : BinaryOperator {
    override fun render(): String = """\times"""
}

object Over : BinaryOperator {
    override fun render(): String = """/"""
}

object Lim0 : Operator {
    override fun render(): String = """\lim_{x \arrow 0}"""
}

object LimInf : Operator {
    override fun render(): String = """\lim_{x \arrow 0}"""
}

object LimNegInf : Operator {
    override fun render(): String = """\lim_{x \arrow 0}"""
}

object LimSupremum : Operator {
    override fun render(): String = """\lim_{x \arrow 0}"""
}

object LimInfimum : Operator {
    override fun render(): String = """\lim_{x \arrow 0}"""
}

object Sqrt : Operator {
    override fun render(): String = """\sqrt"""
}

object Log : Operator {
    override fun render(): String = """\log"""
}

object Inverse : Operator {
    override fun render(): String = """f^{-1}"""
}

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
