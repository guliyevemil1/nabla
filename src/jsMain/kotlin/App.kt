import kotlinx.browser.document
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.button
import kotlinx.html.js.onClickFunction
import org.guliyevemil1.nabla.ExpX
import org.guliyevemil1.nabla.Expr
import org.guliyevemil1.nabla.Multiply
import org.guliyevemil1.nabla.One
import org.guliyevemil1.nabla.Zero
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

data class StateItem(
    val id: String,
    val value: Expr<Any?>,
)

data class Card(val id: Int, val name: String)

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

val expr = Multiply<Any?>(
    One,
    One,
    Zero,
    ExpX,
)

object GameState {
    val items = mutableListOf(
        StateItem("item1", expr),
        StateItem("item2", expr),
        StateItem("item3", expr)
    )
    val hand = mutableListOf<Card>()
}

fun renderState() {
    val stateDiv = document.getElementById("gameState") as HTMLDivElement
    stateDiv.innerHTML = ""

    GameState.items.forEach { item ->
        stateDiv.append {
            div {
                +"${item.id}: "
                val v = div {}
                renderMath(item.value.render(), v)
            }
        }
    }
}

fun renderHand() {
    val handDiv = document.getElementById("hand") as HTMLDivElement
    handDiv.innerHTML = ""

    GameState.hand.forEach { card ->
        handDiv.append {
            val b = button {
                +card.name
                onClickFunction = { playCard(card.id) }
            }
            renderMath(card.name, b)
        }
    }
}

fun playCard(cardId: Int) {
    val card = GameState.hand.find { it.id == cardId }
    if (card != null) {
        // Apply your card effect here
        console.log("Playing card: ${card.name}")

        GameState.hand.remove(card)
        renderHand()
    }
}

fun addCard(card: Card) {
    GameState.hand.add(card)
    renderHand()
}

fun updateState(newItems: List<StateItem>) {
    GameState.items.clear()
    GameState.items.addAll(newItems)
    renderState()
}

fun main() {
    // Initial render
    renderState()
    renderHand()

    // Example: Add some cards
    addCard(Card(1, """\frac{1}{2}"""))
    addCard(Card(2, """\int_0^\infty"""))
    addCard(Card(3, """\frac{d}{dx}"""))
}
