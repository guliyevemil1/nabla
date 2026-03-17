import kotlinx.browser.document
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.button
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLDivElement

data class StateItem(val id: String, var value: Int)

data class Card(val id: Int, val name: String)

object GameState {
    val items = mutableListOf(
        StateItem("item1", 0),
        StateItem("item2", 0),
        StateItem("item3", 0)
    )
    val hand = mutableListOf<Card>()
}

fun renderState() {
    val stateDiv = document.getElementById("gameState") as HTMLDivElement
    stateDiv.innerHTML = ""

    GameState.items.forEach { item ->
        stateDiv.append {
            div {
                +"${item.id}: ${item.value}"
            }
        }
    }
}

fun renderHand() {
    val handDiv = document.getElementById("hand") as HTMLDivElement
    handDiv.innerHTML = ""

    GameState.hand.forEach { card ->
        handDiv.append {
            button {
                +card.name
                onClickFunction = { playCard(card.id) }
            }
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
    addCard(Card(1, "Card 1"))
    addCard(Card(2, "Card 2"))
    addCard(Card(3, "Card 3"))
}
