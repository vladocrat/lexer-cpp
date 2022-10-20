import lexer.CppLexer
import file_reader.LexFileReader

fun main(args: Array<String>) {

    val rawFileAsString = LexFileReader.parseFile("sources\\lexer.cpp") ?: return
    val tokenList = CppLexer.analyze(rawFileAsString)
    val tokens = tokenList
        .groupBy { it.type }
        .mapValues { it.value.distinctBy { it.text } }

    println("\n--------- Доступные токены: ------------\n")
//    println(tokens[CppLexer.commentBlock]?.joinToString(separator = System.lineSeparator()))

    println(tokens.values.joinToString(separator = System.lineSeparator()))
}