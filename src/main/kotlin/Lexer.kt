import java.util.LinkedList

object Lexer {

    @JvmStatic
    fun tokenize(input: String): LinkedList<Token> {
        val tokens = LinkedList<Token>()
        var flag: Boolean //флаг изменения type
        var lexem: Lexem? = null //текущее значение type
        var substr = "" //текущее значение data
        var i = 0 //индекс перемещения по строке

        while (input.length > i) {
            flag = false
            substr += input[i] //считываем следующий символ
            for (l in Lexem.values()) { //определяем тип токена
                val pattern = l.pattern
                val matcher = pattern.matcher(substr)
                if (matcher.matches()) {
                    lexem = l
                    flag = true //если определили успешно, то изменяем текущее значение токена и идем дальше
                    break
                }
            }

            if (input[i] == ' ' || input[i] == '\n' || input[i] == '\t') { //если пробел, перенос строки или табуляция, то игнорируем
                if (lexem != null) {
                    tokens.add(Token(lexem, substr.substring(0, substr.length - 1)))
                    lexem = null
                    substr = ""
                }
            }

            if (lexem != null && !flag) { //если тип токена не определился при считывании нового символа, то возвращаемся назад на 1 символ и добавляем токен
                substr = substr.substring(0, substr.length - 1)
                i--
                tokens.add(Token(lexem, substr))
                lexem = null
                substr = ""
            }
            i++
        }

        if (lexem != null) tokens.add(Token(lexem, substr)) //добавляем последний токен
        return tokens
    }
}