package org.guliyevemil1.nabla.card

interface Card {
}

interface Deck {
    val cards: List<Card>
}

class Shuffler(val deck: Deck) {
    private val drawPile: MutableList<Card> = mutableListOf()
    private val discardPile: MutableList<Card> = deck.cards.toMutableList()

    fun draw(): Card {
        if (drawPile.isEmpty()) {
            drawPile.addAll(discardPile)
            drawPile.apply { shuffle() }
            discardPile.clear()
        }
        return drawPile.removeAt(drawPile.size - 1)
    }

    fun draw(n: Int): List<Card> = List(n) { draw() }

    fun discard(card: Card) {
        discardPile.add(card)
    }
}
