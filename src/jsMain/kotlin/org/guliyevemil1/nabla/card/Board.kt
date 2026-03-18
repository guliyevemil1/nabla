package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.math.X
import org.guliyevemil1.nabla.math.integer
import org.guliyevemil1.nabla.math.pow

abstract class Board<C : Card, P : Player<C>>(
    deck: Deck<C>,
) {

    val shuffler: Shuffler<C> = Shuffler(deck)

    abstract val players: Array<P>

}

interface Player<C : Card> {
    val hand: List<C>
}

class NablaPlayer : Player<NablaCard> {
    override val hand: MutableList<NablaCard> = mutableListOf()
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

    private var turn: Int = 0
}

class NablaField {
    val bases = mutableListOf(
        integer(1),
        X,
        pow(X, 2),
    )
}
