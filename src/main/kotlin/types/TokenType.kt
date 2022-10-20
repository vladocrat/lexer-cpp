package types

interface TokenType {

    val ignored: Boolean

    val name: String

    fun match(input: CharSequence, fromIndex: Int): Int
}


