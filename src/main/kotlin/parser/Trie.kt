package parser

import lexer.CppLexer
import model.Token
import types.TokenType

/**
 * Program Structure Interface
 */
enum class PsiElement {
    /**
     * Последовательность [Token] является опредлением функции
     */
    FUNCTION,

    /**
     * Последовательность [Token] является вызывом функции
     */
    FUN_CALL
}

/**
 * Дерево префиксного происка по [Token] для определения [PsiElement]
 */
object Trie {
    private val root = Node()

    init {
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
            PsiElement.FUN_CALL, listOf(
                CppLexer.identifier,
                CppLexer.lb,
            )
        )
    }

    /**
     * Зарегистрировать новую последовательноть [tokenTypes] для определения [element]
     */
    fun insert(element: PsiElement, tokenTypes: List<TokenType>) {
        var currentNode = root
        for (type in tokenTypes) {
            if (currentNode.childNodes[type] == null) {
                currentNode.childNodes[type] = Node()
            }
            currentNode = currentNode.childNodes[type]!!
        }
        currentNode.psiElement = element
    }

    /**
     * Выполнить поиск [PsiElement] по заданной последовательности [tokens]
     *
     * @return [PsiElement] или `null`
     */
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