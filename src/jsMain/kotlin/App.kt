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

fun Board.renderState(element: HTMLDivElement, bases: List<FieldItem>) {
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
                    disabled = !canReceive(base)
                    classes = setOf("field-button")
                    onClickFunction = { play(base).render() }
                }
                renderMath(base.expr, d)
            }
        }
    }
}

fun Board.renderState() {
    renderState(
        document.getElementById("gameState1") as HTMLDivElement,
        players[0].let { p ->
            p.field.map { FieldItem(p, it) }
        }
    )
    renderState(
        document.getElementById("gameState2") as HTMLDivElement,
        players[1].let { p ->
            p.field.map { FieldItem(p, it) }
        }
    )
}

fun Board.renderHand(element: HTMLDivElement, hand: List<HandCard>) {
    element.innerHTML = ""

    hand.forEach { card ->
        element.append {
            val b = button {
                disabled = !canReceive(card)
                classes += setOf("card-button")
                onClickFunction = { play(card).render() }
            }
            renderMath(card.card, b)
        }
    }
}

fun Board.renderHand() {
    renderHand(
        document.getElementById("hand1") as HTMLDivElement,
        players[0].let { p ->
            p.hand.map { HandCard(p, it) }
        },
    )
    renderHand(
        document.getElementById("hand2") as HTMLDivElement,
        players[1].let { p ->
            p.hand.map { HandCard(p, it) }
        },
    )
}

fun Board.render() {
    renderState()
    renderHand()
}

fun main() {
    board(rng = ImmutableRNG(seed = Random.nextLong())).render()
}
