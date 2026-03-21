package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.ImmutableRNG

interface Card

interface Deck<C : Card> {
    val cards: List<C>
}

fun <C> shuffler(
    rng: ImmutableRNG,
    cards: List<C>,
): Shuffler<C> {
    var rng = rng
    val drawPile = cards.toMutableList().apply {
        for (i in lastIndex downTo 1) {
            val (j, newRng) = rng.nextInt(i + 1)
            this[j] = this.set(i, this[j])
            rng = newRng
        }
    }
    return Shuffler(
        rng = rng,
        drawPile = drawPile,
        discardPile = emptyList(),
    )
}

data class Shuffler<C>(
    private val rng: ImmutableRNG,
    private val drawPile: List<C>,
    private val discardPile: List<C>,
) {

    fun draw(): Pair<C, Shuffler<C>> {
        if (drawPile.isEmpty()) {
            val s = shuffler(rng, discardPile)
            return s.draw()
        }
        return drawPile[drawPile.size - 1] to copy(
            drawPile = drawPile.subList(0, drawPile.size - 1),
        )
    }

    fun draw(n: Int): Pair<List<C>, Shuffler<C>> {
        var shuffler = this
        val l = buildList {
            repeat(n) {
                val (c, newShuffler) = shuffler.draw()
                add(c)
                shuffler = newShuffler
            }
        }
        return l to shuffler
    }

    fun discard(card: C): Shuffler<C> = copy(
        discardPile = discardPile + card,
    )
}
