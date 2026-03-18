import kotlinx.browser.document
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.js.button
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.card.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

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
    val board = NablaBoard()
}

fun renderState(element: HTMLDivElement, bases: MutableList<Base>) {
    element.innerHTML = ""

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
        GameState.board.players[0].field,
    )
    renderState(
        document.getElementById("gameState2") as HTMLDivElement,
        GameState.board.players[1].field,
    )
}

fun <C : NablaCard> renderHand(element: HTMLDivElement, hand: MutableList<HandCard<C>>) {
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
        GameState.board.players[0].hand,
    )
    renderHand(
        document.getElementById("hand2") as HTMLDivElement,
        GameState.board.players[1].hand,
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
