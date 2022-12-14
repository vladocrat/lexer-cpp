package types

/**
 * Представляет токен описываемый через [predicate].
 *
 * Лямбда-функция [predicate] подставляется напрямую в [TokenType.match]
 */
class PredicateTokenType(
    name: String,
    ignored: Boolean = false,
    private val predicate: (input: CharSequence, fromIndex: Int) -> Int
) : AbstractTokenType(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int = predicate(input, fromIndex)
}