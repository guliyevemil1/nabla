package org.guliyevemil1.nabla.parser

import org.guliyevemil1.nabla.math.*
import kotlin.text.iterator

class ParseException(message: String) : Exception(message)
sealed class SExpr {

    data class Symbol(val name: String) : SExpr()
    data class SInteger(val value: Int) : SExpr()
    data class SList(val head: Symbol, val tail: List<SExpr>) : SExpr() {
        val size = tail.size
        val firstArg by lazy {
            require(size >= 1)
            tail[0].toExpr()
        }
        val secondArg by lazy {
            require(size >= 2)
            tail[1].toExpr()
        }
    }

    fun toExpr(): Expr<Any?> = when (this) {
        is SInteger -> Integer(value)
        is SList -> when (head.name.lowercase()) {
            "sin" if firstArg == X -> SinX
            "cos" if firstArg == X -> CosX

            "+", "add", "plus" -> Add(tail.map { it.toExpr() })
            "scale" -> Scale(firstArg as Expr<Nothing>, secondArg)
            "*", "multiply", "times" -> {
                if (firstArg.isConstant) return Scale(
                    firstArg as Expr<Nothing>,
                    secondArg
                )
                Multiply(tail.map { it.toExpr() })
            }

            "/", "divide" -> {
                val l = firstArg
                val r = secondArg
                if (l is Integer && r is Integer) return Rational(l.n, r.n)
                return multiply(l, pow(r, NegOne))
            }

            "exp" -> Exp(firstArg)
            "log" -> Log(firstArg)
            "sqrt" -> Pow(firstArg, OneHalf)

            "xpow" -> XPow(firstArg as Expr<Nothing>)
            "pow" -> {
                Pow(firstArg, secondArg as Expr<Nothing>)
            }

            "ddx" -> Differentiate(firstArg)
            "differentiate" -> Differentiate(firstArg)

            "integrate" -> Integrate(firstArg)

            else -> throw IllegalArgumentException("Unrecognized term: ${head.name}")
        }

        is Symbol -> when (name.lowercase()) {
            "bottom" -> Bottom
            "x" -> X
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
    if (input == "") throw ParseException("empty input provided")
    return LispParser(input).parse().toExpr()
}

fun formatLispExpression(input: String): String {
    val result = StringBuilder()
    var indentLevel = 0
    val indentSize = 2

    for (char in input) {
        when (char) {
            '(' -> {
                result.append('\n')
                result.append(" ".repeat(indentLevel))
                result.append(char)
                indentLevel += indentSize
            }

            ')' -> {
                indentLevel -= indentSize
                result.append(char)
            }

            ' ' -> {
                // Skip spaces that are just separators
                if (result.isNotEmpty() && result.last() != '(' && result.last() != '\n') {
                    result.append(char)
                }
            }

            else -> {
                result.append(char)
            }
        }
    }

    return result.toString().trim()
}
