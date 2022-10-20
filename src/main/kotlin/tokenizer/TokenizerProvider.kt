package tokenizer

import types.TokenType

/**
 * Предоставляет конкретный экземпляр [Tokenizer] по списку [TokenType]
 */
fun interface TokenizerProvider<out T : Tokenizer> {
    fun provide(tokenTypes: List<TokenType>): T
}