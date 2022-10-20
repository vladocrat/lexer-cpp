package types

/**
 * Представление токена
 */
interface TokenType {

    /**
     * Данный тип токена должен подлежать синтаксическому анализу?
     */
    val ignored: Boolean

    /**
     * Название токена
     */
    val name: String

    /**
     * Попытка сопоставить "часть текста" с [TokenType]
     *
     * @param input Текст подлежащий анализу
     * @param fromIndex Индекс с которого необходимо начать анализ
     *
     * @return длина совпадающего сегмента или `0`, если совпадения не найдено
     */
    fun match(input: CharSequence, fromIndex: Int): Int
}


