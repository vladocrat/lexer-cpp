package tokenizer

import Config
import types.TokenType

/**
 * Реализация [Tokenizer] find-first-way: на каждом шаге ищет первый совпавший токен
 * Не возвращает токены с [TokenType.ignored] = `true`
 *
 * @param tokenTypes Список допустимых типов токена
 */
class FirstMatchTokenizer(tokenTypes: List<TokenType>) : BaseMatchTokenizer(tokenTypes) {
    override fun findMatch(input: CharSequence, offset: Int): Pair<TokenType, Int>? {
        return tokenTypes.asSequence().mapNotNull { type ->
            val matchedLength = type.match(input, offset)
            if (Config.hasLogg) {
                println(
                    "try-match: ${type.name} | txt=[ ${
                        input.subSequence(offset, input.lastIndex).toString().replace("\n", "\\")
                    } ]"
                )
            }
            if (matchedLength > 0) type to matchedLength else null
        }.firstOrNull()
    }
}