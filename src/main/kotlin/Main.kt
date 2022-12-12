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

object Global {
    val collection = mutableListOf<Token>()
    var lastToken:Token? = null
}

tailrec fun isBalanced(input: List<Token>, stack: Stack<Token>): Boolean = when {
    Global.lastToken != null || input.isEmpty() -> stack.isEmpty()
    else -> {
        val c = input.first()
        if (c.type == CppLexer.lb || c.type == CppLexer.rb) {
            if (stack.isNotEmpty() && matchBrackets(stack.peek().type, c.type)) {
                stack.pop()
                Global.lastToken = c
            } else {
                Global.lastToken = null
                stack.push(c)
            }
        } else {
            Global.lastToken = null
            Global.collection.add(c)
        }
        isBalanced(input.subList(1, input.size), stack)
    }
}

fun matchBrackets(typeA: TokenType, typeB: TokenType,) = when {
    typeA == CppLexer.lb && typeB == CppLexer.rb -> true
    else -> false
}


fun main(args: Array<String>) {

    val rawFileAsString = LexFileReader.parseFile("./sources/main.cpp") ?: return
    CppLexer.parse(rawFileAsString)
    val tokenList = CppLexer.analyze(rawFileAsString)

    val trie = TriePattern().apply {
        insert(
            PsiElement.FUNCTION, listOf(
                CppLexer.primitiveType,
                CppLexer.identifier,
                CppLexer.lb,
            )
        )
        insert(
            PsiElement.FUNCTION, listOf(
                CppLexer.identifier,
                CppLexer.identifier,
                CppLexer.lb,
            )
        )
        insert(
            PsiElement.FUNCTION, listOf(
                CppLexer.primitiveType,
                CppLexer.identifier,
                CppLexer.operatorQualified,
                CppLexer.identifier,
                CppLexer.lb,
            )
        )
        insert(
            PsiElement.FUNCTION, listOf(
                CppLexer.identifier,
                CppLexer.identifier,
                CppLexer.operatorQualified,
                CppLexer.identifier,
                CppLexer.lb,
            )
        )
        insert(
            PsiElement.FUNCTION, listOf(
                CppLexer.identifier,
                CppLexer.operatorQualified,
                CppLexer.identifier,
                CppLexer.lb,
            )
        )
        insert(
            PsiElement.FUNCTION, listOf(
                CppLexer.identifier,
                CppLexer.operatorQualified,
                CppLexer.tilda,
                CppLexer.identifier,
                CppLexer.lb,
                CppLexer.rb,
            )
        )
        insert(
            PsiElement.FUNCTION, listOf(
                CppLexer.tilda,
                CppLexer.identifier,
                CppLexer.lb,
                CppLexer.rb,
            )
        )
        insert(
            PsiElement.LOOP, listOf(
                CppLexer.statementLoop,
                CppLexer.lb,
            )
        )
        insert(
            PsiElement.LOOP, listOf(
                CppLexer.statementLoop,
                CppLexer.lcb,
            )
        )
        insert(PsiElement.CONDITION, listOf( // switch()
            CppLexer.statementControl,
            CppLexer.lb
        ))
        insert(PsiElement.CONDITION, listOf( // (if|else|switch|case) {
            CppLexer.statementControl,
            CppLexer.lcb
        ))
    }


    println("\n--------- Доступные токены ------------")
    val tempContainer = mutableListOf<Token>()

    var index = 0
    while (index < tokenList.size) {
        tempContainer.add(tokenList[index])
        if (trie.startsWith(tempContainer)) {
            val psiElement = trie.findPsiElement(tempContainer)
            if(psiElement != null) {
                println(psiElement)
                println(tempContainer)
                if (psiElement == PsiElement.FUNCTION) {
                    val stack = Stack<Token>()
                    val isBalanced = isBalanced(tokenList.subList(index, tokenList.size), stack)
                    if (isBalanced) {
                        println(Global.collection.joinToString { it.text })
                        val lastToken = Global.lastToken!!
                        index = tokenList.indexOf(lastToken)
                    } else {
                        println(stack.joinToString())
                        throw RuntimeException("Sosi")
                    }
                } else {
                    index++
                }
            } else {
                index++
            }
        } else {
            index++
            tempContainer.clear()
        }
    }

//    tokenList
//        .onEach {
//            // fun definition
//            if (it.type == CppLexer.primitiveType) TODO()
//            if (it.type == CppLexer.identifier) TODO()
//            if (it.type == CppLexer.lb) TODO()
//            if (it.type == CppLexer.rb) TODO()
//            if (it.type == CppLexer.semicolon) TODO()
//        }
//        .onEach {
//            // fun
//            if (it.type == CppLexer.primitiveType) TODO()
//            if (it.type == CppLexer.identifier) TODO()
//            if (it.type == CppLexer.lb) TODO()
//            if (it.type == CppLexer.rb) TODO()
//            if (it.type == CppLexer.lcb) TODO()
//            // BODY
//            if (it.type == CppLexer.rcb) TODO()
//        }
}