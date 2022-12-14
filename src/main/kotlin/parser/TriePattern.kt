package parser

import lexer.CppLexer
import model.Token
import types.TokenType

enum class PsiElement {
    FUNCTION, LOOP, CONDITION
}

object TriePattern {

    private val root = Node()
    val availableTypes = mutableSetOf<TokenType>()

    fun init() {
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
        insert(
            PsiElement.CONDITION, listOf( // switch()
                CppLexer.statementControl,
                CppLexer.lb
            )
        )
        insert(
            PsiElement.CONDITION, listOf( // (if|else|switch|case) {
                CppLexer.statementControl,
                CppLexer.lcb
            )
        )
//        insert(
//            PsiElement.FUN_CALL, listOf( // (if|else|switch|case) {
//                CppLexer.identifier,
//                CppLexer.lcb
//            )
//        )
    }

    fun insert(element: PsiElement, tokenTypes: List<TokenType>) {
        var currentNode = root
        for (type in tokenTypes) {
            availableTypes.add(type)
            if (currentNode.childNodes[type] == null) {
                currentNode.childNodes[type] = Node()
            }
            currentNode = currentNode.childNodes[type]!!
        }
        currentNode.psiElement = element
    }
//
//    fun search(word: String): Boolean {
//        var currentNode = root
//        for (char in word) {
//            if (currentNode.childNodes[char] == null) {
//                return false
//            }
//            currentNode = currentNode.childNodes[char]!!
//        }
//        return currentNode.token != null
//    }

    fun findPsiElement(tokens: List<Token>): PsiElement? {
        var currentNode = root
        for (token in tokens) {
            if (currentNode.childNodes[token.type] == null) {
                return null
            }
            currentNode = currentNode.childNodes[token.type]!!
        }
        return currentNode.psiElement
    }

    fun startsWith(tokens: List<Token>): Boolean {
        var currentNode = root
        for (token in tokens) {
            if (currentNode.childNodes[token.type] == null) {
                return false
            }
            currentNode = currentNode.childNodes[token.type]!!
        }
        return currentNode.childNodes.isNotEmpty() || currentNode.psiElement != null
    }

    data class Node(
        var psiElement: PsiElement? = null,
        val childNodes: MutableMap<TokenType, Node> = mutableMapOf()
    )

}