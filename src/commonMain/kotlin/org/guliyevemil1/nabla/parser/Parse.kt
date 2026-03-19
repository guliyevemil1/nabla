package org.guliyevemil1.nabla.parser

import org.guliyevemil1.nabla.math.*

class ParseException(message: String) : Exception(message)

class Parser(private val input: String) {
    private var pos = 0

    companion object {
        fun parse(input: String): Expr<Any?> = Parser(input).parse()
    }

    fun parse(): Expr<Any?> {
        skipWhitespace()
        val expr = parseExpression()
        skipWhitespace()
        if (pos < input.length) {
            throw ParseException("Unexpected character at position $pos: '${input[pos]}'")
        }
        return expr
    }

    private fun parseExpression(): Expr<Any?> {
        skipWhitespace()

        // Try to parse a number first
        val numberExpr = tryParseNumber()
        if (numberExpr != null) {
            return numberExpr
        }

        // Otherwise parse a symbol
        val symbol = parseSymbol()
        return checkForApplication(symbol)
    }

    private fun checkForApplication(funcName: String): Expr<Any?> {
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

            return when (funcName) {
                "Add", "Plus" -> Add(args)
                "Multiply" -> Multiply(args)
                "Divide" -> require(args.size == 2).let {
                    Divide(args[0], args[1])
                }

                "Pow" -> require(args.size == 2 && args[1] is Integral).let {
                    if (args[0] == X)
                        xPow(args[1] as Integral)
                    else
                        pow(args[0], (args[1] as Integer).n)
                }

                "Differentiate" -> require(args.size == 1).let {
                    Differentiate(args[0])
                }

                "Integrate" -> require(args.size == 1).let {
                    Integrate(args[0])
                }

                "Sqrt" -> require(args.size == 1).let {
                    Sqrt(args[0])
                }

                "Log" -> require(args.size == 1).let {
                    Log(args[0])
                }

                "Cos" -> require(args.size == 1 && args[0] == X).let {
                    CosX
                }

                "Sin" -> require(args.size == 1 && args[0] == X).let {
                    SinX
                }

                "Exp" -> require(args.size == 1 && args[0] == X).let {
                    ExpX
                }

//                "Limit" -> Limit()

                else -> Illegal
            }
        }

        return when (funcName) {
            "ExpX" -> ExpX
            "SinX" -> SinX
            "CosX" -> CosX
            "X", "x" -> X
            else -> throw ParseException("Unexpected symbol at position $pos: '${funcName}'")
        }
    }

    private fun parseArguments(): List<Expr<Any?>> {
        val args = mutableListOf<Expr<Any?>>()
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

    private fun parseSymbol(): String {
        skipWhitespace()
        val start = pos

        if (pos >= input.length || !input[pos].isLetter()) {
            throw ParseException("Expected symbol at position $pos")
        }

        while (pos < input.length && (input[pos].isLetterOrDigit() || input[pos] == '_')) {
            pos++
        }

        return input.substring(start, pos)
    }

    private fun tryParseNumber(): Expr<Nothing>? {
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

        val numberStr = input.substring(start, pos)
        return try {
            integer(numberStr.toInt())
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

fun parse(input: String): Expr<Any?> {
    return Parser(input).parse()
}
