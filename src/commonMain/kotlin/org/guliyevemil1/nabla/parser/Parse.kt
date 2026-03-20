package org.guliyevemil1.nabla.parser

import org.guliyevemil1.nabla.math.*

class ParseException(message: String) : Exception(message)
sealed class SExpr {

    data class Symbol(val name: String) : SExpr()
    data class SInteger(val value: Int) : SExpr()
    data class SList(val head: Symbol, val tail: List<SExpr>) : SExpr() {
        val size = tail.size
    }

    private fun SList.argIsX() = size == 1 && tail[0].toExpr() == X

    fun toExpr(): Expr<Any?> = when (this) {
        is SInteger -> Integer(value)
        is SList -> when (head.name.lowercase()) {
            "+", "add", "plus" -> Add(tail.map { it.toExpr() })
            "*", "multiply", "times" -> Multiply(tail.map { it.toExpr() })
            "sin" if argIsX() -> SinX
            "cos" if argIsX() -> CosX
            "exp" if argIsX() -> ExpX
            "xpow" if size == 1 -> XPow(tail[0].toExpr() as Expr<Nothing>)
            else -> throw IllegalArgumentException("Unrecognized term: ${head.name}")
        }

        is Symbol -> when (name.lowercase()) {
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
