import kotlinx.browser.document
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.js.button
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.p
import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.card.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.random.Random

@JsName("katex")
external object KaTeX {
    fun render(tex: String, element: HTMLElement, options: dynamic = definedExternally)
}

fun renderMath(formula: Expr<*>, element: HTMLElement) {
    KaTeX.render(formula.render(), element)
}

fun renderMath(card: NablaCard, element: HTMLElement) {
    KaTeX.render(card.render(), element)
}

object GameState {
    val board = board(
        rng = ImmutableRNG(seed = Random.nextLong()),
    )
}

fun renderState(element: HTMLDivElement, bases: List<FieldItem>) {
    element.innerHTML = ""

    if (bases.isEmpty()) {
        element.append {
            div {
                +"Game over! The other player won!"
            }
        }
        return
    }

    bases.forEach { base ->
        element.append {
            div {
                val d = button {
                    disabled = !GameState.board.canReceive(base)
                    classes = setOf("field-button")
                    onClickFunction = { play(base) }
                }
                renderMath(base.expr, d)
            }
        }
    }
}

fun renderState() {
    renderState(
        document.getElementById("gameState1") as HTMLDivElement,
        GameState.board.players[0].let { p ->
            p.field.map { FieldItem(p, it) }
        }
    )
    renderState(
        document.getElementById("gameState2") as HTMLDivElement,
        GameState.board.players[1].let { p ->
            p.field.map { FieldItem(p, it) }
        }
    )
}

fun renderHand(element: HTMLDivElement, hand: List<HandCard>) {
    element.innerHTML = ""

    hand.forEach { card ->
        element.append {
            val b = button {
                disabled = !GameState.board.canReceive(card)
                classes += setOf("card-button")
                onClickFunction = { play(card) }
            }
            renderMath(card.card, b)
        }
    }
}

fun renderHand() {
    renderHand(
        document.getElementById("hand1") as HTMLDivElement,
        GameState.board.players[0].let { p ->
            p.hand.map { HandCard(p, it) }
        },
    )
    renderHand(
        document.getElementById("hand2") as HTMLDivElement,
        GameState.board.players[1].let { p ->
            p.hand.map { HandCard(p, it) }
        },
    )
}

fun render() {
    renderState()
    renderHand()
}

fun play(card: Clickable) {
    GameState.board.play(card)
    render()
}

fun main() {
    // Initial render
    render()
}
