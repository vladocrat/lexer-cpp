package types

import lexer.Lexer

/**
 * Предоставляет токен типа [T] по имени [TokenTypeProvider.provide].
 * Требуется брать имена из свойств при построении [Lexer] и предоставлять типы токенов.
 */
abstract class TokenTypeProvider<T : TokenType> {
    protected var isIgnored: Boolean = false
        private set

    /**
     * Предоставить токен типа [T] по [name]
     *
     * @param name Название токена
     */
    abstract fun provide(name: String): T

    /**
     * Отметить, что данный тип токена должен или не должен подлежать синтаксическому анализу
     *
     * @param ignored Флаг
     */
    fun ignored(ignored: Boolean): TokenTypeProvider<T> = apply {
        isIgnored = ignored
    }
}

//Функции синтаксического сахара для создания типизированных экземпляров [TokenTypeProvider]

fun regex(
    regex: String,
    options: Set<RegexOption> = setOf(),
    ignored: Boolean = false
): TokenTypeProvider<RegexTokenType> = object : TokenTypeProvider<RegexTokenType>() {
    override fun provide(name: String): RegexTokenType = RegexTokenType(
        regex = regex,
        name = name,
        options = options,
        ignored = isIgnored
    )
}.ignored(ignored)

fun char(
    char: Char,
    ignoreCase: Boolean = false,
    ignored: Boolean = false
): TokenTypeProvider<CharTokenType> = object : TokenTypeProvider<CharTokenType>() {
    override fun provide(name: String): CharTokenType = CharTokenType(
        char = char,
        name = name,
        ignoreCase = ignoreCase,
        ignored = isIgnored
    )
}.ignored(ignored)

fun predicateTokenType(
    name: String,
    ignored: Boolean,
    predicate: (input: CharSequence) -> Int
): PredicateTokenType = PredicateTokenType(name, ignored) { input, fromIndex ->
    predicate(charSequenceView(input, fromIndex))
}

private fun charSequenceView(charSequence: CharSequence, fromIndex: Int): CharSequence = object : CharSequence {
    override val length: Int = charSequence.length - fromIndex

    override fun get(index: Int): Char = charSequence[index + fromIndex]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return charSequence.subSequence(startIndex + fromIndex, endIndex + fromIndex)
    }
}