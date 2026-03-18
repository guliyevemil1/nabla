package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.card.BoardState.*
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

sealed interface BoardState {
    object None : BoardState

    class StateBinaryOperator(val binaryOperator: BinaryOperator, val finalize: () -> Unit) : BoardState

    class StateBinaryOperatorPartial(val binaryOperator: BinaryOperator, val rhs: BaseCard, val finalize: () -> Unit) :
        BoardState

    class StateOperator(val card: Operator, val finalize: () -> Unit) : BoardState
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

    private var state: BoardState = None

    fun play(clickable: Clickable) {
        val s = state
        when (clickable) {
            is Base -> {
                val base = clickable
                when (s) {
                    is StateOperator -> {
                        base.expr = s.card.transformExpr(base.expr)
                        s.finalize()
                    }

                    is StateBinaryOperatorPartial -> {
                        base.expr = s.binaryOperator.transformExpr(base.expr, s.rhs.expr)
                        s.finalize()
                    }

                    else -> {
                        println("Awaiting hand card")
                        return
                    }
                }
                state = None
                advanceTurn()
            }

            is HandCard<*> -> {
                if (state is StateOperator || state is StateBinaryOperatorPartial) {
                    println("Awaiting base")
                    return
                }

                if (state is StateBinaryOperator && clickable.card !is BaseCard) {
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

                val finalize = {
                    players[turn].hand.removeAt(cardIndex)
                    players[turn].hand.add(HandCard(player = clickable.player, shuffler.draw()))
                    shuffler.discard(clickable.card as NablaCard)
                }

                when (clickedCard) {
                    is BaseCard -> {
                        when (s) {
                            None -> {
                                clickable.player.addBase(clickedCard.expr)
                                advanceTurn()
                            }

                            is StateOperator -> {
                                throw IllegalStateException()
                            }

                            is StateBinaryOperator -> {
                                state = StateBinaryOperatorPartial(
                                    binaryOperator = s.binaryOperator,
                                    rhs = clickedCard,
                                    finalize = {
                                        s.finalize()
                                        finalize()
                                    },
                                )
                                return
                            }

                            is StateBinaryOperatorPartial -> {
                                throw IllegalStateException()
                            }
                        }

                    }

                    is AllOperator -> {
                        players[turn].field.forEach { base ->
                            base.expr = clickedCard.transformExpr(base.expr)
                        }
                        advanceTurn()
                    }

                    is Operator -> {
                        state = StateOperator(clickedCard, finalize)
                        return
                    }

                    is BinaryOperator -> {
                        state = StateBinaryOperator(clickedCard, finalize)
                        return
                    }
                }

                finalize()
            }
        }
    }
}

class Base(
    override val player: NablaPlayer,
    var expr: Expr<Any?>,
) : Clickable
