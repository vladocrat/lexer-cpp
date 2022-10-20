package tokenizer

import Config
import error.TokenizeException
import model.Location
import model.Token
import types.TokenType

interface Tokenizer {

    fun tokenize(input: CharSequence): TokenProducer
}

abstract class BestMatchTokenizer(protected val tokenTypes: List<TokenType>) : Tokenizer {

    init {
        require(tokenTypes.isNotEmpty()) { "Tokens types must be non-empty." }
    }

    override fun tokenize(input: CharSequence): TokenProducer = object : TokenProducer {

        init {
            if (Config.hasLogg) {
                println()
                println(input.toString().replace("\n", "\\"))
                println()
            }
        }

        val state = State()

        override fun nextToken(): Token? = nextNotIgnoredToken(input, state)
    }

    protected abstract fun findBestMatch(input: CharSequence, offset: Int): Pair<TokenType, Int>?

    private fun nextToken(input: CharSequence, state: State): Token? {
        while (true) {
            if (state.offset >= input.length) {
                return null
            }

            val bestMatch = findBestMatch(input, state.offset) ?: throw TokenizeException(
                "Cannot tokenize the whole input. Unknown token: row=${state.row}, column=${state.column}"
            )
            val (type, length) = bestMatch
            val (offset, row, column) = state

            if (Config.hasLogg) {
                println(
                    "  match=${type.name} | txt=[ ${
                        input.subSequence(offset, offset + length).toString().replace("\n", "\\")
                    } ] | isIgnored=${type.ignored}"
                )
            }
            repeat(length) { state.advance(input[offset + it]) }
            if (Config.hasLogg) {
                println(state)
            }
            if (!type.ignored) {
                return Token(type, input, length, Location(offset, row, column))
            }
        }
    }

    private fun nextNotIgnoredToken(input: CharSequence, state: State): Token? {
        while (true) {
            if (Config.hasLogg) {
                println()
                println(state)
            }
            val next = nextToken(input, state)
            if (next == null) {
                require(input.length == state.offset) { "Cannot tokenize the whole input. Unknown token: row=${state.row}, column=${state.column}" }
                return null
            } else if (!next.type.ignored) {
                return next
            }
        }
    }

    private data class State(
        var offset: Int = 0,
        var row: Int = 1,
        var column: Int = 1,
    ) {
        fun advance(symbol: Char) {
            if (symbol == '\n') {
                row++
                column = 0
            }
            column++
            offset++
        }
    }
}