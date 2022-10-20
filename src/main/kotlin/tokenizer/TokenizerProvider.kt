package tokenizer

import types.TokenType

interface TokenizerProvider<out T : Tokenizer> {
    fun provide(tokenTypes: List<TokenType>): T
}

fun <T : Tokenizer> provideTokenizer(provider: (List<TokenType>) -> T): TokenizerProvider<T> {
    return object : TokenizerProvider<T> {
        override fun provide(tokenTypes: List<TokenType>): T = provider(tokenTypes)
    }
}