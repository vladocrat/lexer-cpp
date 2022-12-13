import lexer.CppLexer
import file_reader.LexFileReader
import model.Token
import parser.PsiElement
import parser.TriePattern
import types.TokenType
import java.lang.RuntimeException
import java.util.Stack

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
    TriePattern.init()
    CppLexer.parse(rawFileAsString)
}