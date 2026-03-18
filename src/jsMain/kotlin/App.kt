import kotlinx.browser.document
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.button
import kotlinx.html.js.onClickFunction
import org.guliyevemil1.nabla.Expr
import org.guliyevemil1.nabla.Zero
import org.guliyevemil1.nabla.card.*
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
        StateItem("item1", Zero),
        StateItem("item2", Zero),
        StateItem("item3", Zero)
    )
    val hand = mutableListOf<NablaCard>()
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
    // Initial render
    renderState()
    renderHand()

    // Example: Add some cards
    addCard(One)
    addCard(X)
    addCard(X2)
    addCard(ExpX)
    addCard(SinX)
    addCard(CosX)
    addCard(Nabla)
    addCard(Nabla2)
    addCard(DDx)
    addCard(Integrate)
    addCard(Times)
    addCard(Over)
    addCard(Sqrt)
    addCard(Log)
    addCard(Inverse)
    addCard(Lim0)
    addCard(LimInf)
    addCard(LimNegInf)
    addCard(LimSupremum)
    addCard(LimInfimum)
}
