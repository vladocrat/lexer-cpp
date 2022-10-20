package types

/**
 * Представляет токен описываемый через [predicate].
 *
 * Лямбда-функция [predicate] подставляется напрямую в [TokenType.match]
 */
class PredicateTokenType(
    override val name: String,
    override val ignored: Boolean = false,
    private val predicate: (input: CharSequence, fromIndex: Int) -> Int
) : TokenType {
    override fun match(input: CharSequence, fromIndex: Int): Int = predicate(input, fromIndex)
}