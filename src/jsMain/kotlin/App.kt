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
    val items = mutableListOf(
        StateItem("item1", integer(1)),
        StateItem("item2", X),
        StateItem("item3", pow(X, 2))
    )
    val hand = mutableListOf<NablaCard>()
}

fun renderState() {
    val stateDiv = document.getElementById("gameState") as HTMLDivElement
    stateDiv.innerHTML = ""

    GameState.items.forEach { item ->
        stateDiv.append {
            div {
                renderMath(item.value.render(), div {})
            }
        }
    }
}

fun renderHand() {
    val handDiv = document.getElementById("hand") as HTMLDivElement
    handDiv.innerHTML = ""

    GameState.hand.forEachIndexed { index, card ->
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

fun addCard(card: NablaCard) {
    GameState.hand.add(card)
    renderHand()
}

fun updateState(newItems: List<StateItem>) {
    GameState.items.clear()
    GameState.items.addAll(newItems)
    renderState()
}

fun main() {
    val deck = Shuffler(deck = NablaDeck())
    repeat(times = 7) {
        GameState.hand.add(deck.draw())
    }
    // Initial render
    renderState()
    renderHand()
}
