package error

import tokenizer.BaseMatchTokenizer
import java.lang.RuntimeException

class UnknownTokenException(state: BaseMatchTokenizer.State) : RuntimeException() {
    override val message: String = "Неизвестный токен: row=${state.row}, column=${state.column}"
}
