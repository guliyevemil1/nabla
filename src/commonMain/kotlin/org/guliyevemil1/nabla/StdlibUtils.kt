package org.guliyevemil1.nabla

import kotlin.random.Random

data class ImmutableRNG(private val seed: Int) {
    private fun nextRng() = ImmutableRNG(Random(seed).nextInt())

    fun nextBits(bitCount: Int): Pair<Int, ImmutableRNG> =
        Random(seed).nextBits(bitCount) to nextRng()

    fun nextInt(until: Int): Pair<Int, ImmutableRNG> =
        Random(seed).nextInt(until) to nextRng()
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
