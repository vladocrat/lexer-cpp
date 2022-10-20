package model

import types.TokenType

/**
 * Класс описывающий понятие семантического токена
 *
 * @property type Тип токена
 * @property input Исходный текст, из которого был создан токен
 * @property length Длина совпадающей части
 * @property location Местонахождение токена
 */
data class Token(
    val type: TokenType,
    val input: CharSequence,
    val length: Int,
    val location: Location
) {

    /**
     * Семантическый токен - часть [input], совпадающая с токеном
     */
    val text: String get() = input.substring(location.offset, location.offset + length)

    override fun toString(): String {
        return "Token: type=${type}, text=${text}"
    }
}