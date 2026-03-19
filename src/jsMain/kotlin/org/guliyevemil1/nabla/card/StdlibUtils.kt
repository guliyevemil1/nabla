package org.guliyevemil1.nabla.card

import kotlin.random.Random

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
