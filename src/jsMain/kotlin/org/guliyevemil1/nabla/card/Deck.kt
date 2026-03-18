package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.Expr
import org.guliyevemil1.nabla.X
import org.guliyevemil1.nabla.integer
import org.guliyevemil1.nabla.pow
import kotlin.repeat

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

abstract class Board<C : Card, P : Player<C>>(
    deck: Deck<C>,
) {

    val shuffler: Shuffler<C> = Shuffler(deck)

    abstract val players: Array<P>

}

class NablaBoard : Board<NablaCard, NablaPlayer>(NablaDeck()) {
    override val players = Array(2) {
        NablaPlayer().also { p ->
            repeat(times = 7) {
                p.hand.add(shuffler.draw())
            }
        }
    }

    val fields = Array(2) { NablaField() }
}

class NablaField {
    val bases = mutableListOf(
        integer(1),
        X,
        pow(X, 2),
    )
}
