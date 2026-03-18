package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.math.X
import org.guliyevemil1.nabla.math.integer
import org.guliyevemil1.nabla.math.pow

interface Clickable {
    val player: NablaPlayer
}

abstract class Board<C : Card, P : Player<C>>(
    deck: Deck<C>,
) {

    val shuffler: Shuffler<C> = Shuffler(deck)

    abstract val players: Array<P>

}

interface Player<C : Card> {
    val hand: List<HandCard<C>>
}

class HandCard<C : Card>(
    override val player: NablaPlayer,
    val card: C,
) : Clickable

class NablaPlayer : Player<NablaCard> {
    override val hand: MutableList<HandCard<NablaCard>> = mutableListOf()

    val field: MutableList<Base> = mutableListOf(
        base(integer(1)),
        base(X),
        base(pow(X, 2)),
    )

    private fun base(expr: Expr<Any?>): Base = Base(player = this, expr = expr)

    fun addBase(expr: Expr<Any?>) {
        if (expr == integer(0)) return
        field.add(base(expr))
    }
}

enum class BoardState {
    None,
    AwaitingBaseCard,
    AwaitingBase,
}

class NablaBoard : Board<NablaCard, NablaPlayer>(NablaDeck()) {
    private var turn: Int = 0

    fun advanceTurn() {
        turn = (turn + 1) % 2
        println("turn: $turn")
    }

    override val players = Array(2) {
        NablaPlayer().also { p ->
            repeat(times = 7) {
                p.hand.add(HandCard(player = p, shuffler.draw()))
            }
        }
    }

    private var state: BoardState = BoardState.None

    fun play(clickable: Clickable) {
        when (clickable) {
            is Base -> {
                if (state != BoardState.AwaitingBase) {
                    println("Awaiting hand card")
                    return
                }
                state = BoardState.None
                println("resetting state $state")
                advanceTurn()
            }

            is HandCard<*> -> {
                if (state == BoardState.AwaitingBase) {
                    println("Awaiting base")
                    return
                }

                if (state == BoardState.AwaitingBaseCard && clickable.card !is BaseCard) {
                    println("Awaiting base card")
                    return
                }

                if (clickable.player != players[turn]) {
                    println("It is $turn player's turn")
                    return
                }
                val cardIndex: Int = players[turn].hand.indexOfFirst { it.card == clickable.card }
                    .takeIf { it != -1 }
                    ?: run {
                        println("Card not found")
                        return
                    }

                val clickedCard = clickable.card

                when (clickedCard) {
                    is BaseCard -> {
                        when (state) {
                            BoardState.None -> {
                                clickable.player.addBase(clickedCard.expr)
                                advanceTurn()
                            }

                            BoardState.AwaitingBase -> {
                                throw IllegalStateException()
                            }

                            BoardState.AwaitingBaseCard -> {
                                state = BoardState.None
                                advanceTurn()
                            }
                        }

                    }

                    is AllOperator -> {
                        players[turn].field.forEachIndexed { index, base ->
                            players[turn].field[index].expr = clickedCard.transformExpr(base.expr)
                        }
                        advanceTurn()
                    }

                    is Operator -> {
                        state = BoardState.AwaitingBase
                    }

                    is BinaryOperator -> {
                        state = BoardState.AwaitingBaseCard
                    }
                }

                players[turn].hand.removeAt(cardIndex)
                players[turn].hand.add(HandCard(player = clickable.player, shuffler.draw()))
                shuffler.discard(clickable.card as NablaCard)
            }
        }
    }
}

class Base(
    override val player: NablaPlayer,
    var expr: Expr<Any?>,
) : Clickable
