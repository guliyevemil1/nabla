package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.card.BoardState.*
import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.math.X
import org.guliyevemil1.nabla.math.integer
import org.guliyevemil1.nabla.math.pow
import kotlin.random.Random

interface Clickable {
    val player: Player
}

class HandCard(
    override val player: Player,
    val card: NablaCard,
) : Clickable

data class FieldItem(
    override val player: Player,
    val expr: Expr<Any?>,
) : Clickable

val startingField: List<Expr<Any?>> = listOf(
    integer(1),
    X,
    pow(X, 2),
)

data class Players(
    val player1: Player,
    val player2: Player,
) {
    operator fun get(index: Int): Player = when (index) {
        0 -> player1
        1 -> player2
        else -> throw IndexOutOfBoundsException()
    }
}

class Player(
    hand: List<NablaCard> = listOf(),
    field: List<Expr<Any?>> = listOf(),
) {
    val hand = hand.map { HandCard(this, it) }
    val field = field.map { FieldItem(this, it) }
}

sealed interface BoardState {

    object None : BoardState
    object GameOver : BoardState

    class StateBinaryOperator(
        val binaryOperator: BinaryOperator,
        val playedCards: List<NablaCard>,
    ) : BoardState

    class StateBinaryOperatorPartial(
        val binaryOperator: BinaryOperator,
        val rhs: BaseCard,
        val playedCards: List<NablaCard>,
    ) :
        BoardState

    class StateOperator(
        val card: Operator,
        val playedCards: List<NablaCard>,
    ) : BoardState
}

class Board(
    seed: Long? = null,
    private val shuffler: Shuffler<NablaCard> = Shuffler(
        deck = NablaDeck(),
        rng = seed?.let(::Random) ?: Random.Default
    ),
    private val previous: Board? = null,
    private val state: BoardState = None,
    private val players: Array<Player> = Array(2) {
        Player(
            hand = shuffler.draw(7),
            field = startingField,
        )
    },
) {
    fun copy(
        state: BoardState,
        players: Array<Player>,
    ): Board = Board(
        shuffler = shuffler,
        previous = this,
        state = state,
        players = players,
    )

    private val turn: Int = when {
        previous == null -> 0
        state == None -> 1 - previous.turn
        else -> previous.turn
    }

    fun canReceive(clickable: Clickable): Boolean {
        return when (state) {
            None -> {
                clickable is HandCard && clickable.player == players[turn]
            }

            is StateBinaryOperator -> {
                clickable is HandCard && clickable.card is BaseCard && clickable.player == players[turn]
            }

            is StateBinaryOperatorPartial, is StateOperator -> {
                clickable is FieldItem
            }

            GameOver -> false
        }
    }

    fun play(clickable: Clickable): Board {
        val s = state

        if (!canReceive(clickable)) return this

        val playedCards: List<NablaCard>? = (clickable as? HandCard)?.let { card ->
            players[turn].hand.indexOfFirst { it.card == card.card }
                .takeIf { it != -1 }
                ?: run {
                    println("can't find card")
                    return@let emptyList()
                }

            return@let listOf(card.card)
        }

        when (s) {
            is None -> {
                val handCard = clickable as? HandCard ?: throw IllegalStateException()

                if (handCard.player != players[turn]) {
                    println("It is $turn player's turn")
                    return this
                }

                val clickedCard = handCard.card

                when (clickedCard) {
                    is BaseCard -> {
                        handCard.player.addBase(clickedCard.expr)
                        finalize!!.invoke()
                    }

                    is AllOperator -> {
                        players[1 - turn].field.forEach { base ->
                            base.expr = clickedCard.transformExpr(base.expr)
                        }
                        finalize!!.invoke()
                        return copy(
                            state = None,
                            players = players,
                        )
                    }

                    is Operator -> {
                        return copy(
                            state = StateOperator(clickedCard, playedCards = playedCards!!),
                            players = null,
                        )
                    }

                    is BinaryOperator -> {
                        return copy(
                            state = StateBinaryOperator(clickedCard, playedCards = playedCards!!),
                            players = null,
                        )
                    }
                }
            }

            is StateBinaryOperator -> {
                val base =
                    clickable as? HandCard ?: throw IllegalStateException("did not get a hand card as expected")

                state = StateBinaryOperatorPartial(
                    binaryOperator = s.binaryOperator,
                    rhs = base.card as? BaseCard ?: throw IllegalStateException("did not get a base card as expected"),
                    playedCards = s.playedCards + playedCards,
                )
            }

            is StateOperator -> {
                val fieldItem = clickable as? FieldItem
                    ?: throw IllegalStateException("did not get a base input as expected")
                fieldItem.expr = s.card.transformExpr(fieldItem.expr)
                s.finalize()
                state = None
            }

            is StateBinaryOperatorPartial -> {
                val fieldItem = clickable as? FieldItem
                    ?: throw IllegalStateException("did not get a base input as expected")
                val newField = fieldItem.player.field.map {
                    if (it == fieldItem) {
                        fieldItem.copy(
                            expr = s.binaryOperator.transformExpr(fieldItem.expr, s.rhs.expr)
                        )
                    } else {
                        fieldItem.player
                    }
                }
                s.finalize()
                state = None
            }

            GameOver -> throw IllegalStateException("game is over")
        }
    }
}
