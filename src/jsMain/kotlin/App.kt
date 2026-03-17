package org.guliyevemil1

fun main() {
    println("Hello from Kotlin/JS!")

    // DOM manipulation example
    val element = kotlinx.browser.document.getElementById("app")
    element?.innerHTML = "<h1>Welcome to Kotlin/JS!</h1>"

    // Call a custom function
    greetUser("Developer")
}

fun greetUser(name: String) {
    println("Hello, $name!")

    // You can use Kotlin features like string templates, data classes, etc.
    val message = "Kotlin compiles to: ${getTargetPlatform()}"
    kotlinx.browser.window.alert(message)
}

fun getTargetPlatform(): String {
    return "JavaScript"
}

// Data classes work great
data class User(val name: String, val age: Int)

fun exampleWithDataClass() {
    val user = User("Alice", 30)
    println("User: ${user.name}, Age: ${user.age}")
}
