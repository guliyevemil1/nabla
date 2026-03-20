package org.guliyevemil1.nabla.parser

import org.guliyevemil1.nabla.math.*

class ParseException(message: String) : Exception(message)
sealed class SExpr {

    data class Symbol(val name: String) : SExpr()
    data class SInteger(val value: Int) : SExpr()
    data class SList(val head: Symbol, val tail: List<SExpr>) : SExpr() {
        val size = tail.size
        val firstArg by lazy { tail[0].toExpr() }
        val secondArg by lazy { tail[1].toExpr() }
    }

    private fun SList.argIsX() = size == 1 && tail[0].toExpr() == X

    fun toExpr(): Expr<Any?> = when (this) {
        is SInteger -> Integer(value)
        is SList -> when (head.name.lowercase()) {
            "sin" if argIsX() -> SinX
            "cos" if argIsX() -> CosX
            "exp" if argIsX() -> ExpX

            "+", "add", "plus" -> Add(tail.map { it.toExpr() })
            "scale" if size == 2 -> Scale(firstArg as Expr<Nothing>, secondArg)
            "*", "multiply", "times" -> Multiply(tail.map { it.toExpr() })
            "/" if size == 2 -> {
                val l = firstArg
                val r = secondArg
                if (l is Integer && r is Integer) return Rational(l.n, r.n)
                return Divide(l, r)
            }

            "log" if size == 1 -> Log(firstArg)
            "sqrt" if size == 1 -> Pow(firstArg, OneHalf)

            "xpow" if size == 1 -> XPow(firstArg as Constant)
            "pow" if size == 2 -> Pow(firstArg, secondArg as Constant)

            "ddx" if size == 1 -> Differentiate(firstArg)
            "differentiate" if size == 1 -> Differentiate(firstArg)

            "integrate" if size == 1 -> Integrate(firstArg)

            else -> throw IllegalArgumentException("Unrecognized term: ${head.name}")
        }

        is Symbol -> when (name.lowercase()) {
            "bottom" -> Bottom
            "x" -> X
            "expx" -> ExpX
            "sinx" -> SinX
            "cosx" -> CosX
            else -> throw IllegalArgumentException("Unrecognized term: $name")
        }
    }
}

private class LispParser(private val input: String) {
    private var pos = 0

    fun parse(): SExpr {
        skipWhitespace()
        return parseExpr().also {
            skipWhitespace()
            if (pos < input.length) throw ParseException("Parsing failed: ${input.substring(pos, input.length)}")
        }
    }

    private fun parseExpr(): SExpr {
        skipWhitespace()

        return when {
            pos >= input.length -> throw ParseException("Unexpected end of input")
            input[pos] == '(' -> parseList()
            else -> parseAtom()
        }
    }

    private fun parseList(): SExpr {
        if (input[pos] != '(') {
            throw ParseException("Expected '(' at position $pos")
        }
        pos++ // consume '('

        val elements = mutableListOf<SExpr>()

        while (true) {
            skipWhitespace()

            if (pos >= input.length) {
                throw ParseException("Unclosed list")
            }

            if (input[pos] == ')') {
                pos++ // consume ')'
                break
            }

            elements.add(parseExpr())
        }

        return SExpr.SList(head = elements[0] as SExpr.Symbol, elements.drop(1))
    }

    private fun parseAtom(): SExpr {
        val start = pos

        // Handle negative numbers
        if (input[pos] == '-' && pos + 1 < input.length && input[pos + 1].isDigit()) {
            pos++
        }

        while (pos < input.length && !input[pos].isWhitespace() && input[pos] !in "()") {
            pos++
        }

        if (start == pos) {
            throw ParseException("Expected atom at position $pos")
        }

        val token = input.substring(start, pos)

        // Try to parse as integer
        return token.toIntOrNull()?.let { SExpr.SInteger(it) }
            ?: SExpr.Symbol(token)
    }

    private fun skipWhitespace() {
        while (pos < input.length && input[pos].isWhitespace()) {
            pos++
        }
    }
}

fun parse(input: String): Expr<Any?> {
    return LispParser(input).parse().toExpr()
}
