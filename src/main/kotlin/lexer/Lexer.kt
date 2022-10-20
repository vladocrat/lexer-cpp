package lexer

import model.Token
import tokenizer.Tokenizer
import tokenizer.TokenizerProvider
import types.TokenType
import types.TokenTypeProvider
import kotlin.reflect.KProperty

/**
 * Базовый класс лексера.
 */
abstract class Lexer {

    /**
     * Поставщик [TokenizerProvider]
     */
    abstract val tokenizerProvider: TokenizerProvider<*>

    /**
     * [Tokenizer]
     */
    val tokenizer: Tokenizer by lazy { tokenizerProvider.provide(tokenTypes) }

    /**
     * Список допустимых типов токена.
     * Порядок элементов имеет значение и определяется порядком добавления [TokenType] в список.
     */
    protected val tokenTypes: MutableList<TokenType> = mutableListOf()

    /**
     * Выполнить лексический анализ [input]
     *
     * @return Список токенов
     */
    fun analyze(input: CharSequence): List<Token> {
        println("-> Запущен анализ токенов")
        val tokenProducer = tokenizer.tokenize(input)
        return tokenProducer.toList().also { println("-> Анализ завершен") }
    }

    // Функции синтаксического сахара для автоматического заполнения списка [tokenTypes] через объявления делегированных свойств класса

    protected operator fun <T : TokenType> TokenTypeProvider<T>.provideDelegate(
        thisRef: Lexer,
        kProperty: KProperty<*>
    ): T = provide(kProperty.name).provideDelegate(thisRef, kProperty)

    protected operator fun <T : TokenType> T.provideDelegate(
        thisRef: Lexer,
        kProperty: KProperty<*>
    ): T = apply { tokenTypes += this }

    protected operator fun <T : Lexer> T.provideDelegate(
        thisRef: Lexer,
        kProperty: KProperty<*>
    ): T = apply {
        this@Lexer.tokenTypes += this@provideDelegate.tokenTypes
    }

    protected operator fun <T : TokenType> T.getValue(thisRef: Lexer, kProperty: KProperty<*>): T = this

    protected operator fun <T : Lexer> T.getValue(thisRef: Lexer, kProperty: KProperty<*>): T = this
}