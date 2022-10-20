package types

class CharTokenType(
    private val char: Char,
    override val name: String,
    private val ignoreCase: Boolean = false,
    override val ignored: Boolean = false,
) : TokenType {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        return if (input[fromIndex].equals(char, ignoreCase)) 1 else 0
    }

    override fun toString() = name
}