import kotlinx.browser.document
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.js.button
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

fun renderState(element: HTMLDivElement, field: NablaField) {
    element.innerHTML = ""

    field.bases.forEach { item ->
        element.append {
            val d = button {
                classes = setOf("field-button")
            }
            renderMath(item, d)
        }
    }
}

fun renderState() {
    renderState(
        document.getElementById("gameState1") as HTMLDivElement,
        GameState.board.fields[0],
    )
    renderState(
        document.getElementById("gameState2") as HTMLDivElement,
        GameState.board.fields[1],
    )
}

fun renderHand(element: HTMLDivElement, hand: MutableList<NablaCard>) {
    element.innerHTML = ""

    hand.forEach { card ->
        element.append {
            val b = button {
                classes += setOf("card-button")
                onClickFunction = { playCard(hand, card) }
            }
            renderMath(card, b)
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

fun playCard(hand: MutableList<out Card>, card: Card) {
    val card = hand.find { it == card }
    if (card != null) {
        // Apply your card effect here
        console.log("Playing card: $card")
        hand.remove(card)
        renderHand()
    }
}

fun main() {
    // Initial render
    renderState()
    renderHand()
}
