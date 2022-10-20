package model

import types.TokenType

data class Token(

    val type: TokenType,

    val input: CharSequence,

    val length: Int,

    val location: Location
) {

    val text: String get() = input.substring(location.offset, location.offset + length)

    override fun toString(): String {
        return "Token: type=${type}, text=${text}"
    }
}