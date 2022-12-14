package parser

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import model.Token
import serializer.TokenListSerializer


/**
 * Элемент абстрактного синтаксического дерева
 */
@Serializable
sealed interface ASTElement {

    @Serializable
    data class FunctionCall(
        val content: List<Token>
    ): ASTElement


    @Serializable
    data class CodeLine(
        @Serializable(with = TokenListSerializer::class)
        val content: List<Token>
    ) : ASTElement {
        override fun toString(): String {
            return content.joinToString(" ") { it.text }
        }
    }

    @Serializable
    sealed interface BlockContent : ASTElement {
        var content: List<ASTElement>
        @Transient
        var isContentSet: Boolean

        @Serializable
        data class While(
            var token: Token,
            @Serializable(with = TokenListSerializer::class)
            var condition: List<Token>,
            override var content: List<ASTElement>,
            @Transient
            var isConditionSet: Boolean = false,
            @Transient
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${token.text} (${condition.joinToString("") { it.text }}) { $content }"
            }
        }

        @Serializable
        data class For(
            var token: Token,
            @Serializable(with = TokenListSerializer::class)
            var definition: List<Token>?,
            override var content: List<ASTElement>,
            @Transient
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${token.text} (${definition?.joinToString("") { it.text }}) { $content }"
            }
        }

        @Serializable
        data class If(
            var token: Token,
            @Serializable(with = TokenListSerializer::class)
            var condition: List<Token>,
            override var content: List<ASTElement>,
            @Transient
            var isConditionSet: Boolean = false,
            @Transient
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${token.text} (${condition.joinToString("") { it.text }}) { $content }"
            }
        }

        @Serializable
        data class Else(
            var token: Token,
            override var content: List<ASTElement>,
            @Transient
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${token.text} { $content }"
            }
        }

        @Serializable
        data class Function(
            @Serializable(with = TokenListSerializer::class)
            var definition: List<Token>,
            @Serializable(with = TokenListSerializer::class)
            var arguments: List<Token>,
            override var content: List<ASTElement>,
            @Transient
            var isArgumentsSet: Boolean = false,
            @Transient
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${definition.joinToString(" ") { it.text }} (${arguments.joinToString("") { it.text }}) { $content }"
            }
        }
    }
}