package types

/**
 * Представляет токен, который описывается регулярным выражением [regex].
 */
class RegexTokenType(
    regex: String,
    override val name: String,
    options: Set<RegexOption> = emptySet(),
    override val ignored: Boolean = false,
) : TokenType {
    private val matcher = "^$regex".toRegex(options)

    override fun match(input: CharSequence, fromIndex: Int): Int {
        val match = matcher.find(input.subSequence(fromIndex, input.length))
        return if (match != null) match.range.last - match.range.first + 1 else 0
    }
    override fun toString() = name
}