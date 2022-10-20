package tokenizer

import Config
import error.UnknownTokenException
import model.Location
import model.Token
import types.TokenType

/**
 * Преобразование текста в [TokenProducer]
 */
fun interface Tokenizer {

    /**
     * Преобразовать [CharSequence] в [TokenProducer]
     */
    fun tokenize(input: CharSequence): TokenProducer
}

/**
 * Базовый класс [Tokenizer]
 * На каждом шаге ищется токен из [tokenTypes] через совпадение (см. [findMatch]).
 * Не возвращает токены с [TokenType.ignored] = `true`
 *
 * @property tokenTypes Список допустимых типов токена
 */
abstract class BaseMatchTokenizer(protected val tokenTypes: List<TokenType>) : Tokenizer {

    init {
        require(tokenTypes.isNotEmpty()) { "Типы токенов не должны быть пустыми" }
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

    /**
     * Попытка сопоставить "часть текста" с [TokenType]
     *
     * @param input Текст подлежащий анализу
     * @param offset Смещение с которого необходимо начать анализ
     *
     * @return наиболее подходящий [TokenType] и длину совпадающего сегмента или `null`,
     * если совпадения не найдено среди всех [tokenTypes]
     */
    protected abstract fun findMatch(input: CharSequence, offset: Int): Pair<TokenType, Int>?

    private fun nextToken(input: CharSequence, state: State): Token? {
        while (true) {
            if (state.offset >= input.length) return null

            val match = findMatch(input, state.offset) ?: throw UnknownTokenException(state)
            val (type, length) = match
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
                return Token(type, input, length, Location(row, column, offset))
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
                if(input.length != state.offset) throw UnknownTokenException(state)
                return null
            } else if (!next.type.ignored) {
                return next
            }
        }
    }

    /**
     * Внутренне состояние [BaseMatchTokenizer]
     * для отслеживания позиции последнего проанализированного сегмента текста
     */
    data class State(
        var offset: Int = 0,
        var row: Int = 1,
        var column: Int = 1,
    ) {

        /**
         * Изменить состояние через сдвиг
         */
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