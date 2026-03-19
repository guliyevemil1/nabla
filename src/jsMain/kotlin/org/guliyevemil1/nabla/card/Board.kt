package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.card.BoardState.*
import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.math.Illegal
import org.guliyevemil1.nabla.math.X
import org.guliyevemil1.nabla.math.X2
import org.guliyevemil1.nabla.math.Zero
import org.guliyevemil1.nabla.math.integer

interface Clickable {
    val player: Player
}

class HandCard(
    override val player: Player,
    val idx: Int,
    val card: NablaCard,
) : Clickable

data class FieldItem(
    override val player: Player,
    val idx: Int,
    val expr: Expr<Any?>,
) : Clickable

val startingField: List<Expr<Any?>> = listOf(
    integer(1),
    X,
    X2,
)

data class Players(
    val player1: Player = Player(idx = 0),
    val player2: Player = Player(idx = 1),
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

    fun containsIllegal(): Boolean =
        Illegal in player1.field || Illegal in player2.field
}

data class Player(
    val idx: Int,
    val hand: List<NablaCard> = listOf(),
    val field: List<Expr<Any?>> = startingField,
) {
    fun draw(s: Shuffler<NablaCard>): Pair<Player, Shuffler<NablaCard>> {
        val (cs, s2) = s.draw(7 - hand.size)
        return with(hand = hand + cs) to s2
    }

    fun with(
        hand: List<NablaCard> = this.hand,
        field: List<Expr<Any?>> = this.field,
    ) = copy(
        hand = hand.sortedWith(NablaCardComparator),
        field = field,
    )
}

sealed interface BoardState {

    object None : BoardState
    object GameOver : BoardState

    class StateBinaryOperator(
        val binaryOperator: BinaryOperator,
        val playedCard: Int,
    ) : BoardState

    class StateBinaryOperatorPartial(
        val binaryOperator: BinaryOperator,
        val rhs: BaseCard,
        val playedCards: List<Int>,
    ) :
        BoardState

    class StateOperator(
        val card: Operator,
        val playedCard: Int,
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
            state = state,
            previous = previous,
        )
    }

    fun undo(): Board {
        return previous ?: this
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
    ).draw()

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
        transformOther: Player.() -> Player = { this },
        transformIdx: Player.() -> Player = { this },
    ) = when (turn) {
        0 -> copy(
            player1 = player1.transform().transformIdx(),
            player2 = player2.transformOther().transformIdx(),
        )

        1 -> copy(
            player1 = player1.transformOther().transformIdx(),
            player2 = player2.transform().transformIdx(),
        )

        else -> throw IllegalStateException("unrecognized turn: $turn")
    }

    fun Players.checkGameOver() = if (player1.field.contains(Illegal) || player2.field.contains(Illegal)) {
        update(transform = { with(field = emptyList()) })
    } else {
        this
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
                                    with(
                                        hand = hand.filterIndexed { index, _ -> handCard.idx != index },
                                        field = (field + clickedCard.expr),
                                    )
                                },
                            ),
                        )
                    }

                    is AllOperator -> {
                        return copy(
                            state = None,
                            players = players.update(
                                transform = { with(hand = hand.filterIndexed { index, _ -> handCard.idx != index }) },
                                transformOther = {
                                    with(field = field.mapNotNull {
                                        clickedCard.transform(it).takeIf { it != Zero }
                                    })
                                }
                            ),
                        )
                    }

                    is Operator -> {
                        return copy(state = StateOperator(clickedCard, playedCard = handCard.idx))
                    }

                    is BinaryOperator -> {
                        return copy(state = StateBinaryOperator(clickedCard, playedCard = handCard.idx))
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
                        playedCards = listOf(s.playedCard, handCard!!.idx),
                    ),
                )
            }

            is StateOperator -> {
                val fieldItem = clickable as? FieldItem
                    ?: throw IllegalStateException("did not get a base input as expected")
                return copy(
                    state = None,
                    players = players.update(
                        transform = { with(hand = hand.filterIndexed { index, _ -> s.playedCard != index }) },
                        transformIdx = {
                            if (fieldItem.player.idx == idx) {
                                with(field = field.replaceAt(fieldItem.idx) {
                                    s.card.transform(fieldItem.expr).takeIf { it != Zero }
                                }.filterNotNull())
                            } else {
                                this
                            }
                        }
                    ).checkGameOver(),
                )
            }

            is StateBinaryOperatorPartial -> {
                val fieldItem = clickable as? FieldItem
                    ?: throw IllegalStateException("did not get a base input as expected")
                return copy(
                    state = None,
                    players = players.update(
                        transform = { with(hand = hand.filterIndexed { index, _ -> index !in s.playedCards }) },
                        transformIdx = {
                            if (fieldItem.player.idx == idx) {
                                with(
                                    field = field.replaceAt(fieldItem.idx) {
                                        s.binaryOperator.transform(fieldItem.expr, s.rhs.expr)
                                            .takeIf { it != Zero }
                                    }.filterNotNull()
                                )
                            } else {
                                this
                            }
                        },
                    ).checkGameOver(),
                )
            }

            GameOver -> throw IllegalStateException("game is over")
        }
    }
}
