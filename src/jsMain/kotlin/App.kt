import kotlinx.browser.document
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.button
import kotlinx.html.js.onClickFunction
import org.guliyevemil1.nabla.Expr
import org.guliyevemil1.nabla.Pow
import org.guliyevemil1.nabla.X
import org.guliyevemil1.nabla.card.*
import org.guliyevemil1.nabla.integer
import org.guliyevemil1.nabla.pow
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

data class StateItem(
    val id: String,
    val value: Expr<Any?>,
)

// KaTeX external interface
@JsName("katex")
external object KaTeX {
    fun render(tex: String, element: HTMLElement, options: dynamic = definedExternally)
}

fun renderMath(formula: String, element: HTMLElement) {
    KaTeX.render(formula, element)
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

fun renderHand() {
    val handDiv = document.getElementById("hand") as HTMLDivElement
    handDiv.innerHTML = ""

    GameState.board.players[0].hand.forEachIndexed { index, card ->
        handDiv.append {
            val b = button {
                classes += setOf("card-button")
                onClickFunction = { playCard(index) }
            }
            renderMath(card, b)
        }
    }
}

fun playCard(cardId: Int) {
//    val card = GameState.hand.find { it.id == cardId }
//    if (card != null) {
//        // Apply your card effect here
//        console.log("Playing card: ${card.name}")
//
//        GameState.hand.remove(card)
//        renderHand()
//    }
}

fun main() {
    // Initial render
    renderState()
    renderHand()
}
