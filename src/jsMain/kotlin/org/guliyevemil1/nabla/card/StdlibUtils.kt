package org.guliyevemil1.nabla.card

import kotlin.random.Random

data class ImmutableRNG(val seed: Int) {
    fun nextInt(until: Int): Pair<Int, ImmutableRNG> {
        val random = Random(seed)
        val value = random.nextInt(until)
        // Return next random generator to be used in next iteration
        return Pair(value, ImmutableRNG(value))
    }
}

fun <T, U : T> List<U>.replaceAt(index: Int, item: U): List<T> =
    this.toMutableList().apply { set(index, item) }.toList()

fun <T, U : T> List<U>.replaceAt(index: Int, transform: (U) -> T): List<T> =
    this.replaceAt(index, transform(this[index]))
