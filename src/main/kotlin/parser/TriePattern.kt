package parser

import model.Token
import types.TokenType

enum class PsiElement {
    FUNCTION, LOOP, CONDITION
}

class TriePattern {

    private val root = Node()
    val availableTypes = mutableSetOf<TokenType>()

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