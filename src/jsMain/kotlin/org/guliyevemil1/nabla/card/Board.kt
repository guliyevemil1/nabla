package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.card.BoardState.*
import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.math.X
import org.guliyevemil1.nabla.math.integer
import org.guliyevemil1.nabla.math.pow

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
    val player1: Player = Player(),
    val player2: Player = Player(),
) {
    operator fun get(index: Int): Player = when (index) {
        0 -> player1
        1 -> player2
        else -> throw IndexOutOfBoundsException()
    }

    fun draw(s0: Shuffler<NablaCard>): Pair<Players, Shuffler<NablaCard>> {
        val (p1, s1) = player1.draw(s0)
        val (p2, s2) = player2.draw(s1)
        return Players(
            player1 = p1,
            player2 = p2,
        ) to s2
    }
}

data class Player(
    val hand: List<NablaCard> = listOf(),
    val field: List<Expr<Any?>> = startingField,
) {
    fun draw(s: Shuffler<NablaCard>): Pair<Player, Shuffler<NablaCard>> {
        val (cs, s2) = s.draw(7 - hand.size)
        return copy(hand = hand + cs) to s2
    }
}

sealed interface BoardState {

    object None : BoardState
    object GameOver : BoardState

    class StateBinaryOperator(
        val binaryOperator: BinaryOperator,
        val playedCard: HandCard,
    ) : BoardState

    class StateBinaryOperatorPartial(
        val binaryOperator: BinaryOperator,
        val rhs: BaseCard,
        val playedCards: List<HandCard>,
    ) :
        BoardState

    class StateOperator(
        val card: Operator,
        val playedCard: HandCard,
    ) : BoardState
}

fun board(rng: ImmutableRNG): Board {
    val s = shuffler(rng = rng, cards = NablaDeck.cards)
    return Board(shuffler = s).draw()
}

class Board(
    private val shuffler: Shuffler<NablaCard>,
    private val state: BoardState = None,
    val players: Players = Players(),
    val previous: Board? = null,
) {

    fun draw(): Board {
        val (ps, s1) = players.draw(shuffler)
        return Board(
            shuffler = s1,
            players = ps,
        )
    }

    fun copy(
        shuffler: Shuffler<NablaCard> = this.shuffler,
        state: BoardState,
        players: Players = this.players,
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

    fun Players.update(
        transform: Player.() -> Player,
    ): Players = when (turn) {
        0 -> copy(player1 = player1.transform())
        1 -> copy(player2 = player2.transform())
        else -> throw IllegalStateException("unrecognized turn: $turn")
    }

    fun play(clickable: Clickable): Board {
        val s = state

        if (!canReceive(clickable)) return this

        val handCard: HandCard? = (clickable as? HandCard)?.let { card ->
            players[turn].hand.indexOfFirst { it == card.card }
                .takeIf { it != -1 }
                ?: run {
                    println("can't find card")
                    return@let null
                }

            return@let card
        }

        when (s) {
            is None -> {
                if (handCard!!.player != players[turn]) {
                    println("It is $turn player's turn")
                    return this
                }

                val clickedCard = handCard.card

                when (clickedCard) {
                    is BaseCard -> {
                        return copy(
                            state = None,
                            shuffler = shuffler.discard(clickedCard),
                            players = players.update(
                                transform = {
                                    copy(
                                        hand = hand - clickedCard,
                                    )
                                },
                            ),
                        )
                    }

                    is AllOperator -> {
                        return copy(
                            state = None,
                            players = players.update(
                                transform = {
                                    copy(field = field.map { clickedCard.transformExpr(it) })
                                }
                            ),
                        )
                    }

                    is Operator -> {
                        return copy(
                            state = StateOperator(clickedCard, playedCard = handCard),
                        )
                    }

                    is BinaryOperator -> {
                        return copy(
                            state = StateBinaryOperator(clickedCard, playedCard = handCard),
                        )
                    }
                }
            }

            is StateBinaryOperator -> {
                val base =
                    clickable as? HandCard ?: throw IllegalStateException("did not get a hand card as expected")

                return copy(
                    state = StateBinaryOperatorPartial(
                        binaryOperator = s.binaryOperator,
                        rhs = base.card as? BaseCard
                            ?: throw IllegalStateException("did not get a base card as expected"),
                        playedCards = listOf(s.playedCard, handCard!!),
                    ),
                )
            }

            is StateOperator -> {
                val fieldItem = clickable as? FieldItem
                    ?: throw IllegalStateException("did not get a base input as expected")
                return copy(
                    state = None,
                    players = players.update {
                        copy(
//                            field = field.map { expr ->
//                                if (expr == fieldItem.expr) {
//                                    s.card.transformExpr(expr)
//                                } else {
//                                    expr
//                                }
//                            }
                        )
                    }
                )
            }

            is StateBinaryOperatorPartial -> {
                val fieldItem = clickable as? FieldItem
                    ?: throw IllegalStateException("did not get a base input as expected")
//                val newField = fieldItem.player.field.map {
//                    if (it == fieldItem) {
//                        fieldItem.copy(
//                            expr = s.binaryOperator.transformExpr(fieldItem.expr, s.rhs.expr)
//                        )
//                    } else {
//                        fieldItem.player
//                    }
//                }
//                s.finalize()
//                state = None
                return this
            }

            GameOver -> throw IllegalStateException("game is over")
        }
    }
}
