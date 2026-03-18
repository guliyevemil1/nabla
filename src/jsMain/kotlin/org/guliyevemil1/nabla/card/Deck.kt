package org.guliyevemil1.nabla.card

interface Card {
}

interface Deck<C : Card> {
    val cards: List<C>
}

class Shuffler<C : Card>(val deck: Deck<C>) {
    private val drawPile: MutableList<C> = mutableListOf()
    private val discardPile: MutableList<C> = deck.cards.toMutableList()

    fun draw(): C {
        if (drawPile.isEmpty()) {
            drawPile.addAll(discardPile)
            drawPile.apply { shuffle() }
            discardPile.clear()
        }
        return drawPile.removeAt(drawPile.size - 1)
    }

    fun draw(n: Int): List<C> = List(n) { draw() }

    fun discard(card: C) {
        discardPile.add(card)
    }
}
