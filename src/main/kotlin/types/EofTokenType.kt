package types

object EofTokenType : AbstractTokenType("EOF", false) {

    override fun match(input: CharSequence, fromIndex: Int): Int {
        throw IllegalStateException("EOF token must not be used to be matched.")
    }
}