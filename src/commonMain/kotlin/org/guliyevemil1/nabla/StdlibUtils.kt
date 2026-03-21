package org.guliyevemil1.nabla

import kotlin.random.Random
import kotlin.text.iterator

data class ImmutableRNG(private val seed: Int) {
    fun nextBits(bitCount: Int): Pair<Int, ImmutableRNG> {
        return Pair(
            Random(seed).nextBits(bitCount),
            ImmutableRNG(Random(seed).nextInt())
        )
    }

    fun nextInt(until: Int): Pair<Int, ImmutableRNG> {
        return Pair(
            Random(seed).nextInt(until),
            ImmutableRNG(Random(seed).nextInt())
        )
    }
}

fun <T, U : T> List<U>.replaceAt(index: Int, item: U): List<T> =
    this.toMutableList().apply { set(index, item) }.toList()

fun <T, U : T> List<U>.replaceAt(index: Int, transform: (U) -> T): List<T> =
    this.replaceAt(index, transform(this[index]))

fun <T> List<T>.groupWith(predicate: (T, T) -> Boolean): List<List<T>> =
    foldIndexed(mutableListOf<MutableList<T>>()) { index, acc, value ->
        if (index == 0 || !predicate(this[index - 1], value)) {
            acc.add(mutableListOf(value))
        } else {
            acc.last().add(value)
        }
        acc
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
