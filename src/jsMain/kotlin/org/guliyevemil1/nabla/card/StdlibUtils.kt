package org.guliyevemil1.nabla.card

import kotlin.random.Random

data class ImmutableRNG(val seed: Int) {
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
