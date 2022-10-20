import lexer.CppLexer
import file_reader.LexFileReader

fun main(args: Array<String>) {

    val rawFileAsString = LexFileReader.parseFile("sources\\lexer.cpp") ?: return
    val tokenList = CppLexer.analyze(rawFileAsString)

    println("\n--------- Доступные токены ------------")
    val tokens = tokenList
        .groupBy { it.type }
        .run {
            val keyNamePadding = keys.maxOf { it.name.length }
            mapValues { (_, tokens) -> tokens.groupingBy { it.text }.eachCount() }
                .run {
                    val arrSizePadding = maxOf { it.value.size.toString().length }
                    map { (type, group) ->
                        type.name.padEnd(keyNamePadding) +
                                group
                                    .map { "'${it.key}'=${it.value}" }
                                    .joinToString(
                                        prefix = ": (${group.size.toString().padStart(arrSizePadding, '0')})[",
                                        postfix = "]"
                                    )
                    }
                }
        }.also { println("<тип токенов>: (<кол-во токенов>)[ <'токен'=частота вхождения в файле>, ...]\n") }

    println(tokens.joinToString(separator = System.lineSeparator()))
}