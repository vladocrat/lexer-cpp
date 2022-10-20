package types


abstract class TokenTypeProvider<T : TokenType> {
    protected var isIgnored: Boolean = false
        private set

    abstract fun provide(name: String): T

    fun ignored(value: Boolean = true): TokenTypeProvider<T> = apply {
        isIgnored = value
    }
}

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