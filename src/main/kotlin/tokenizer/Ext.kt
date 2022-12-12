package tokenizer

import model.Token

fun TokenProducer.indexed(): IndexedTokenProducer = when (this) {
    is IndexedTokenProducer -> this
    else -> object : IndexedTokenProducer {
        private val tokens = mutableListOf<Token>()

        override val lastToken: Token?
            get() = tokens.lastOrNull()

        override fun nextToken(): Token? = this@indexed.nextToken()?.also { tokens += it }

        override fun get(index: Int): Token {
            require(index >= 0) { "Index must be non-negative." }
            return requireNotNull(getOrNull(index)) {
                "Cannot get a token by index $index: too big."
            }
        }

        override fun getOrNull(index: Int): Token? {
            if (index < 0) {
                return null
            }
            while (tokens.size < index + 1) {
                if (nextToken() == null) {
                    break
                }
            }
            return tokens.getOrNull(index).also { println(index) }
        }
    }
}