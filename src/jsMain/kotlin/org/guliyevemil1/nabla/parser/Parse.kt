package org.guliyevemil1.nabla.parser

sealed class Expr {
    data class Symbol(val name: String) : Expr() {
        override fun toString() = name
    }

    data class Number(val value: Double) : Expr() {
        override fun toString() = value.toString()
    }

    data class Application(val function: Expr, val args: List<Expr>) : Expr() {
        override fun toString() = "$function[${args.joinToString(", ")}]"
    }
}

class ParseException(message: String) : Exception(message)

class Parser(private val input: String) {
    private var pos = 0

    fun parse(): Expr {
        skipWhitespace()
        val expr = parseExpression()
        skipWhitespace()
        if (pos < input.length) {
            throw ParseException("Unexpected character at position $pos: '${input[pos]}'")
        }
        return expr
    }

    private fun parseExpression(): Expr {
        skipWhitespace()

        // Try to parse a number first
        val numberExpr = tryParseNumber()
        if (numberExpr != null) {
            return checkForApplication(numberExpr)
        }

        // Otherwise parse a symbol
        val symbol = parseSymbol()
        return checkForApplication(symbol)
    }

    private fun checkForApplication(expr: Expr): Expr {
        skipWhitespace()

        // Check if followed by '['
        if (pos < input.length && input[pos] == '[') {
            pos++ // consume '['
            val args = parseArguments()
            skipWhitespace()
            if (pos >= input.length || input[pos] != ']') {
                throw ParseException("Expected ']' at position $pos")
            }
            pos++ // consume ']'

            val application = Expr.Application(expr, args)
            // Check for nested applications like f[x][y]
            return checkForApplication(application)
        }

        return expr
    }

    private fun parseArguments(): List<Expr> {
        val args = mutableListOf<Expr>()
        skipWhitespace()

        // Handle empty argument list
        if (pos < input.length && input[pos] == ']') {
            return args
        }

        while (true) {
            args.add(parseExpression())
            skipWhitespace()

            if (pos >= input.length) {
                throw ParseException("Unexpected end of input while parsing arguments")
            }

            when (input[pos]) {
                ',' -> {
                    pos++ // consume ','
                    skipWhitespace()
                }

                ']' -> break
                else -> throw ParseException("Expected ',' or ']' at position $pos, got '${input[pos]}'")
            }
        }

        return args
    }

    private fun parseSymbol(): Expr.Symbol {
        skipWhitespace()
        val start = pos

        if (pos >= input.length || !input[pos].isLetter()) {
            throw ParseException("Expected symbol at position $pos")
        }

        while (pos < input.length && (input[pos].isLetterOrDigit() || input[pos] == '_')) {
            pos++
        }

        return Expr.Symbol(input.substring(start, pos))
    }

    private fun tryParseNumber(): Expr.Number? {
        skipWhitespace()
        val start = pos

        if (pos >= input.length) return null

        // Handle optional negative sign
        if (input[pos] == '-') {
            pos++
        }

        if (pos >= input.length || !input[pos].isDigit()) {
            pos = start // reset
            return null
        }

        // Parse integer part
        while (pos < input.length && input[pos].isDigit()) {
            pos++
        }

        // Parse optional decimal part
        if (pos < input.length && input[pos] == '.') {
            pos++
            if (pos < input.length && input[pos].isDigit()) {
                while (pos < input.length && input[pos].isDigit()) {
                    pos++
                }
            }
        }

        val numberStr = input.substring(start, pos)
        return try {
            Expr.Number(numberStr.toDouble())
        } catch (e: NumberFormatException) {
            pos = start // reset
            null
        }
    }

    private fun skipWhitespace() {
        while (pos < input.length && input[pos].isWhitespace()) {
            pos++
        }
    }
}

fun parse(input: String): Expr {
    return Parser(input).parse()
}
