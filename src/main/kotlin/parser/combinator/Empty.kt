package parser.combinator

import parser.OrdinaryParser
import parser.ParseResult
import parser.ParsedValue
import tokenizer.IndexedTokenProducer

object EmptyCombinator : OrdinaryParser<Unit> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<Unit> {
        return ParsedValue(Unit, fromIndex)
    }
}

fun emptyParser(): OrdinaryParser<Unit> = EmptyCombinator