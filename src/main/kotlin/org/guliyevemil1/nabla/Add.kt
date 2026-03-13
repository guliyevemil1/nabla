package org.guliyevemil1.nabla

import org.guliyevemil1.nabla.ConstExpr.Companion.constExpr

fun add(vararg summands: Base) = add(summands.asList())

fun add(summands: List<Base>): Base {
    return when (summands.size) {
        0 -> Zero
        1 -> summands[0]
        else -> summands.sortedBy {
            when (it) {
                is Constant -> 0
                else -> 1
            }
        }.reduce { l, r ->
            if (l is Illegal || r is Illegal) return@reduce Illegal
            if (l is Constant && r is Constant) {
                return@reduce add(l, r)
            }
            TODO()
        }
    }
}

fun add(vararg summands: Constant) = add(summands.asList())

fun add(summands: List<Constant>): Constant {
    return when (summands.size) {
        0 -> Zero
        1 -> summands[0]
        else -> summands.reduce { l, r ->
            if (l is Illegal || r is Illegal) return@reduce Illegal
            if (l is Integer && r is Integer) return@reduce Integer(l.n + r.n)
            val ratL = l.toRational()
            val ratR = l.toRational()
            if (ratL == null || ratR == null) {
                return@reduce constExpr(Add(l, r))
            }
            return@reduce rational(
                numerator = ratL.numerator * ratR.denominator + ratR.numerator * ratL.denominator,
                denominator = ratL.denominator * ratR.denominator,
            )
        }
    }
}

fun multiply(vararg multiplicants: Base) = multiply(multiplicants.asList())

fun multiply(multiplicants: List<Base>): Base {
    return when (multiplicants.size) {
        0 -> One
        1 -> multiplicants[0]
        else -> multiplicants.sortedBy {
            when (it) {
                is Integer -> 0
                is Rational -> 1
                is ConstExpr -> 2
                else -> 3
            }
        }.reduce { l, r ->
            if (l is Illegal || r is Illegal) return@reduce Illegal
            if (l is Constant && r is Constant) {
                return@reduce multiply(l, r)
            }
            TODO()
        }
    }
}

fun multiply(vararg multiplicants: Constant) = multiply(multiplicants.asList())

fun multiply(multiplicants: List<Constant>): Constant {
    return when (multiplicants.size) {
        0 -> One
        1 -> multiplicants[0]
        else -> multiplicants.reduce { l, r ->
            if (l is Illegal || r is Illegal) return@reduce Illegal
            if (l is Integer && r is Integer) return@reduce Integer(l.n * r.n)
            val ratL = l.toRational()
            val ratR = l.toRational()
            if (ratL == null || ratR == null) {
                TODO()
            }
            return@reduce rational(
                numerator = ratL.numerator * ratR.numerator,
                denominator = ratL.denominator * ratR.denominator,
            )
        }
    }
}

fun divide(l: Constant, r: Constant): Constant {
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integer && r is Integer) return rational(l.n, r.n)
    val ratL = l.toRational()
    val ratR = l.toRational()
    if (ratL == null || ratR == null) {
        TODO()
    }
    return rational(
        numerator = ratL.numerator * ratR.denominator,
        denominator = ratL.denominator * ratR.numerator,
    )
}

private val powMap = HashMap<Int, Pow>()

fun pow(base: Constant, n: Int): Constant {
    if (n < 0) return Illegal
    if (n == 0) return One
    return multiply(base, pow(base, n - 1))
}

fun pow(base: Base, n: Int): Pow {
    if (base is X) {
        return powMap.computeIfAbsent(n) { Pow(X, n) }
    }
    return Pow(base, n)
}
