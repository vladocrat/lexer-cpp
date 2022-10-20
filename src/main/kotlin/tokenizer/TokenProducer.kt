package tokenizer

import model.Token

interface TokenProducer : Iterable<Token> {

    fun nextToken(): Token?

    override fun iterator(): Iterator<Token> = object : AbstractIterator<Token>() {
        override fun computeNext() {
            when (val value = nextToken()) {
                null -> done()
                else -> setNext(value)
            }
        }
    }
}