
class Token(val type: Lexem, var data: String) {
    override fun toString(): String {
        return "$type $data"
    }
}