package tokenizer

import Config
import types.TokenType

class FirstMatchTokenizer(tokenTypes: List<TokenType>) : BestMatchTokenizer(tokenTypes) {
    override fun findBestMatch(input: CharSequence, offset: Int): Pair<TokenType, Int>? {
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