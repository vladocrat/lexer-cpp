package types

import model.Location
import model.Token
import parser.MismatchTokenTypeFailure
import tokenizer.IndexedTokenProducer
import parser.ParseResult
import parser.ParsedValue
import parser.unexpectedEofFailure

abstract class AbstractTokenType(
    override val name: String,
    override val ignored: Boolean
) : TokenType {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<Token> {
        val token = tokenProducer.getOrNull(fromIndex)
        return when {
            token == null -> {
                val location = tokenProducer.lastToken?.location ?: Location.Empty // TODO add last token to location
                unexpectedEofFailure(location, this)
            }

            token.type === this -> ParsedValue(token, fromIndex + 1)
            else -> MismatchTokenTypeFailure(token.location, this, token.type)
        }
    }

    override fun toString(): String = name
}