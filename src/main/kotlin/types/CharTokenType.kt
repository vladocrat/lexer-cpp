package types

/**
 * Представляет токен, который сравнивает первый символ с [char] с учетом [ignoreCase].
 */
class CharTokenType(
    private val char: Char,
    name: String,
    private val ignoreCase: Boolean = false,
    ignored: Boolean = false,
) : AbstractTokenType(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        return if (input[fromIndex].equals(char, ignoreCase)) 1 else 0
    }

    override fun toString() = name
}