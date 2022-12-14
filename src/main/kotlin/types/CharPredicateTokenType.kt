package types

/**
 * Представляет токен, который проверяет [predicate] для каждого символа последовательно.
 * Как только [predicate] возвращает `false`, сопоставление завершается и возвращается количество совпавших символов.
 */
class CharPredicateTokenType(
    name: String,
    ignored: Boolean = false,
    private val predicate: (Char) -> Boolean
) : AbstractTokenType(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        val length = input.length
        for (i in fromIndex until length) {
            if (!predicate(input[i])) return i - fromIndex
        }
        return length - fromIndex
    }
    override fun toString() = name
}