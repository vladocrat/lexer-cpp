package file_reader

object LexFileReader {

    fun parseFile(path: String): String? {
        return Escaper().readFile(path)
    }
}
