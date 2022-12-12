package parser.combinator

import parser.ParseResult
import parser.OrdinaryParser
import parser.SkipParser
import tokenizer.IndexedTokenProducer


/**
 * @return [OrdinaryParser] from [parserGetter] which is calculated only once when it is used for the first time.
 * Usually required to allow using not initialized parsers.
 */
fun <T> ref(parserGetter: () -> OrdinaryParser<T>): OrdinaryParser<T> = object : OrdinaryParser<T> {
    private val parser by lazy(parserGetter)

    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<T> {
        return parser.parse(tokenProducer, fromIndex)
    }
}

/**
 * @return [SkipParser] from [parserGetter] which is calculated only once when it is used for the first time.
 * Usually required to allow using not initialized parsers.
 */
fun <T> ref(parserGetter: () -> SkipParser<T>): SkipParser<T> = object : OrdinaryParser<T> {
    private val parser by lazy { parserGetter().inner }

    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<T> {
        return parser.parse(tokenProducer, fromIndex)
    }
}.skip()