package parser.combinator

import error.ParseException
import parser.MismatchTokenTypeFailure
import parser.ParseResult
import parser.OrdinaryParser
import parser.ParsedValue
import tokenizer.IndexedTokenProducer
import types.EofTokenType


fun eofParser(): OrdinaryParser<Unit> = object : OrdinaryParser<Unit> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<Unit> {
        if (fromIndex < 0) {
            throw ParseException("Unexpected value of 'fromIndex=$fromIndex'.")
        }
        return when (val token = tokenProducer.getOrNull(fromIndex)) {
            null -> ParsedValue(Unit, fromIndex)
            else -> MismatchTokenTypeFailure(token.location, EofTokenType, token.type)
        }
    }
}