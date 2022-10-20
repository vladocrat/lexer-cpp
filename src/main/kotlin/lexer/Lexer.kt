package lexer

import model.Token
import tokenizer.Tokenizer
import tokenizer.TokenizerProvider
import types.TokenType
import types.TokenTypeProvider
import kotlin.reflect.KProperty

abstract class Lexer {

    abstract val tokenizerProvider: TokenizerProvider<*>

    val tokenizer: Tokenizer by lazy { tokenizerProvider.provide(tokenTypes) }

    protected val tokenTypes: MutableList<TokenType> = mutableListOf()

    fun analyze(input: CharSequence): List<Token> {
        val tokenProducer = tokenizer.tokenize(input)
        return tokenProducer.toList()
    }

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