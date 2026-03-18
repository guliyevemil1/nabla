package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.Expr
import org.guliyevemil1.nabla.X
import org.guliyevemil1.nabla.integer
import org.guliyevemil1.nabla.pow

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

interface Player<C : Card> {
    val hand: List<C>
}

class NablaPlayer : Player<NablaCard> {
    override val hand: MutableList<NablaCard> = mutableListOf()
}

abstract class Board<C : Card, P : Player<C>> {
    abstract val deck: Deck<C>

    val shuffler: Shuffler<C> = Shuffler(deck)

    abstract val players: List<P>

}

class NablaBoard(n: Int) : Board<NablaCard, NablaPlayer>() {
    override val deck = NablaDeck()

    override val players = List(n) { NablaPlayer() }

    val fields: List<NablaField> = List(n) { NablaField() }
}

class NablaField {
    val bases = mutableListOf(
        integer(1),
        X,
        pow(X, 2),
    )
}
