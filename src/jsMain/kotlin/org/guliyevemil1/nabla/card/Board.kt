package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.card.BoardState.*
import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.math.X
import org.guliyevemil1.nabla.math.integer
import org.guliyevemil1.nabla.math.pow

interface Clickable {
    val player: Player
}

class HandCard<C : Card>(
    override val player: Player,
    val card: C,
) : Clickable

class Player {
    val hand: MutableList<HandCard<NablaCard>> = mutableListOf()

    val field: MutableList<FieldItem> = mutableListOf(
        base(integer(1)),
        base(X),
        base(pow(X, 2)),
    )

    private fun base(expr: Expr<Any?>): FieldItem = FieldItem(player = this, expr = expr)

    fun addBase(expr: Expr<Any?>) {
        if (expr == integer(0)) return
        field.add(base(expr))
    }
}

sealed interface BoardState {

    object None : BoardState
    object GameOver : BoardState

    class StateBinaryOperator(
        val binaryOperator: BinaryOperator,
        val finalize: () -> Unit,
    ) : BoardState

    class StateBinaryOperatorPartial(val binaryOperator: BinaryOperator, val rhs: BaseCard, val finalize: () -> Unit) :
        BoardState

    class StateOperator(val card: Operator, val finalize: () -> Unit) : BoardState
}

class Board {
    val shuffler = Shuffler(NablaDeck())

    private var turn: Int = 0

    fun advanceTurn() {
        turn = (turn + 1) % 2
        println("turn: $turn")
    }

    val players = Array(2) {
        Player().also { p ->
            repeat(times = 7) {
                p.hand.add(HandCard(player = p, shuffler.draw()))
            }
        }
    }

    private var state: BoardState = None

    fun canReceive(clickable: Clickable): Boolean {
        return when (state) {
            None -> {
                clickable is HandCard<*> && clickable.player == players[turn]
            }

            is StateBinaryOperator -> {
                clickable is HandCard<*> && clickable.card is BaseCard && clickable.player == players[turn]
            }

            is StateBinaryOperatorPartial, is StateOperator -> {
                clickable is FieldItem
            }

            GameOver -> false
        }
    }

    fun play(clickable: Clickable) {
        val s = state

        if (!canReceive(clickable)) return

        val finalize = (clickable as? HandCard<*>)?.let { card ->
            val cardIndex: Int = players[turn].hand.indexOfFirst { it.card == card.card }
                .takeIf { it != -1 }
                ?: run {
                    println("can't find card")
                    return@let null
                }

            return@let {
                players[turn].hand.removeAt(cardIndex)
                players[turn].hand.add(HandCard(player = clickable.player, shuffler.draw()))
                players[0].field.removeAll { it.expr == integer(0) }
                players[1].field.removeAll { it.expr == integer(0) }
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
                        players[1 - turn].field.forEach { base ->
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
                val base =
                    clickable as? HandCard<*> ?: throw IllegalStateException("did not get a hand card as expected")

                state = StateBinaryOperatorPartial(
                    binaryOperator = s.binaryOperator,
                    rhs = base.card as? BaseCard ?: throw IllegalStateException("did not get a base card as expected"),
                    finalize = {
                        s.finalize()
                        finalize!!.invoke()
                    },
                )
            }

            is StateOperator -> {
                val fieldItem = clickable as? FieldItem
                    ?: throw IllegalStateException("did not get a base input as expected")
                fieldItem.expr = s.card.transformExpr(fieldItem.expr)
                s.finalize()
                state = None
                advanceTurn()
            }

            is StateBinaryOperatorPartial -> {
                val fieldItem = clickable as? FieldItem
                    ?: throw IllegalStateException("did not get a base input as expected")
                fieldItem.expr = s.binaryOperator.transformExpr(fieldItem.expr, s.rhs.expr)
                s.finalize()
                state = None
                advanceTurn()
            }
        }
    }
}

class FieldItem(
    override val player: Player,
    var expr: Expr<Any?>,
) : Clickable
