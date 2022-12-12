package tokenizer

import model.Token

interface IndexedTokenProducer : TokenProducer {
    /**
     * Returns a token by an index.
     */
    operator fun get(index: Int): Token

    /**
     * Returns a token by index or null if [index] is out of range.
     */
    fun getOrNull(index: Int): Token?

    /**
     * Returns the last produced token or `null` if nothing was produced.
     */
    val lastToken: Token?
}