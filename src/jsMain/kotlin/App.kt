import kotlinx.browser.document
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.js.button
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.p
import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.card.*
import org.guliyevemil1.nabla.math.Illegal
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
            p.field.mapIndexed { index, expr -> FieldItem(p, index, expr) }
        }
    )
    renderState(
        document.getElementById("gameState2") as HTMLDivElement,
        players[1].let { p ->
            p.field.mapIndexed { index, expr -> FieldItem(p, index, expr) }
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
            p.hand.mapIndexed { index, card -> HandCard(p, index, card) }
        },
    )
    renderHand(
        document.getElementById("hand2") as HTMLDivElement,
        players[1].let { p ->
            p.hand.mapIndexed { index, card -> HandCard(p, index, card) }
        },
    )
}

fun Board.renderUndo(element: HTMLDivElement) {
    val b = this
    element.innerHTML = ""
    element.append {
        button {
            classes = setOf("field-button")
            +"Undo"
            onClickFunction = {
                b.undo().render()
            }
        }
    }
}

fun Board.render() {
    renderUndo(document.getElementById("undo") as HTMLDivElement)
    renderState()
    renderHand()
}

fun main() {
    board(rng = ImmutableRNG(seed = Random.nextInt())).render()
}
