package tokenizer

import model.Token

/**
 * Производит токены один за другим (по требованию [Iterator.next])
 */
interface TokenProducer : Iterable<Token> {

    /**
     * Попытка создать новый токен
     *
     * @return [Token] или `null`, если токен не осталось
     */
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