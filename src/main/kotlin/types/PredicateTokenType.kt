package types

class PredicateTokenType(
    override val name: String,
    override val ignored: Boolean = false,
    private val predicate: (input: CharSequence, fromIndex: Int) -> Int
) : TokenType {
    override fun match(input: CharSequence, fromIndex: Int): Int = predicate(input, fromIndex)
}

fun predicateTokenType(
    name: String,
    ignored: Boolean,
    predicate: (input: CharSequence) -> Int
): PredicateTokenType = PredicateTokenType(name, ignored) { input, fromIndex ->
    predicate(charSequenceView(input, fromIndex))
}

private fun charSequenceView(charSequence: CharSequence, fromIndex: Int): CharSequence = object : CharSequence {
    override val length: Int = charSequence.length - fromIndex

    override fun get(index: Int): Char = charSequence[index + fromIndex]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return charSequence.subSequence(startIndex + fromIndex, endIndex + fromIndex)
    }
}