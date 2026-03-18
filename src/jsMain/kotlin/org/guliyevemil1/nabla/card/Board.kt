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

    fun canReceive(clickable: Clickable): Boolean

    object None : BoardState {
        override fun canReceive(clickable: Clickable): Boolean =
            clickable is HandCard<*>
    }

    class StateBinaryOperator(
        val binaryOperator: BinaryOperator,
        val finalize: () -> Unit,
    ) : BoardState {
        override fun canReceive(clickable: Clickable): Boolean = clickable is BaseCard
    }

    class StateBinaryOperatorPartial(val binaryOperator: BinaryOperator, val rhs: BaseCard, val finalize: () -> Unit) :
        BoardState {
        override fun canReceive(clickable: Clickable): Boolean = clickable is Base
    }

    class StateOperator(val card: Operator, val finalize: () -> Unit) : BoardState {
        override fun canReceive(clickable: Clickable): Boolean = clickable is Base
    }
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

        if (!s.canReceive(clickable)) return

        val finalize = (clickable as? HandCard<*>)?.let { card ->
            val cardIndex: Int = players[turn].hand.indexOfFirst { it.card == card.card }
                .takeIf { it != -1 }
                ?: run {
                    println("can't find card")
                    return@let {}
                }

            return@let {
                players[turn].hand.removeAt(cardIndex)
                players[turn].hand.add(HandCard(player = clickable.player, shuffler.draw()))
                shuffler.discard(clickable.card as NablaCard)
            }
        }

        when (s) {
            is None -> {
                val handCard = clickable as? HandCard<*> ?: throw IllegalStateException()

                if (handCard.player != players[turn]) {
                    println("It is $turn player's turn")
                    return
                }

                val clickedCard = handCard.card

                when (clickedCard) {
                    is BaseCard -> {
                        handCard.player.addBase(clickedCard.expr)
                        finalize!!.invoke()
                        advanceTurn()
                    }

                    is AllOperator -> {
                        players[turn].field.forEach { base ->
                            base.expr = clickedCard.transformExpr(base.expr)
                        }
                        finalize!!.invoke()
                        advanceTurn()
                    }

                    is Operator -> {
                        state = StateOperator(clickedCard, finalize!!)
                    }

                    is BinaryOperator -> {
                        state = StateBinaryOperator(clickedCard, finalize!!)
                    }
                }
            }

            is StateBinaryOperator -> {
                val base = clickable as? BaseCard ?: throw IllegalStateException()

                state = StateBinaryOperatorPartial(
                    binaryOperator = s.binaryOperator,
                    rhs = base,
                    finalize = {
                        s.finalize()
                        finalize!!.invoke()
                    },
                )
            }

            is StateOperator -> {
                val base = clickable as? Base ?: throw IllegalStateException()
                base.expr = s.card.transformExpr(base.expr)
                s.finalize()
                state = None
                advanceTurn()
            }

            is StateBinaryOperatorPartial -> {
                val base = clickable as? Base ?: throw IllegalStateException()
                base.expr = s.binaryOperator.transformExpr(base.expr, s.rhs.expr)
                s.finalize()
                state = None
                advanceTurn()
            }
        }
    }
}

class Base(
    override val player: NablaPlayer,
    var expr: Expr<Any?>,
) : Clickable
