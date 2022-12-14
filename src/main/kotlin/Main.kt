import lexer.CppLexer
import file_reader.LexFileReader
import kotlinx.serialization.json.Json
import model.Token
import kotlinx.serialization.*
import parser.CppParser

fun List<Token>.beautify(): List<String> {
    return this.groupBy { it.type }
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
}

fun main(args: Array<String>) {
    val rawFileAsString = LexFileReader.parseFile("./sources/main.cpp") ?: return

    val tokens = CppLexer.analyze(rawFileAsString)
    val syntaxStack = CppParser.parse(tokens)

    val json = Json {
        prettyPrint = true
        useArrayPolymorphism = true
    }
    println(json.encodeToString(syntaxStack))
}