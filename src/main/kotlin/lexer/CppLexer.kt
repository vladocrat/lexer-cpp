package lexer

import model.Token
import parser.PsiElement
import parser.TriePattern
import tokenizer.FirstMatchTokenizer
import tokenizer.TokenizerProvider
import types.char
import types.regex
import types.TokenType
import java.util.*

/**
 * Лексер для файлов языка С++
 */
object CppLexer : Lexer() {
    override val tokenizerProvider = TokenizerProvider { FirstMatchTokenizer(it) }

    val commentBlock by regex("/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/")
    val commentLine by regex("//.*+")
    val directiveInclude by regex("[^\\S\\r\\n]*#include(?=[^\\S\\r\\n]*)")
    val directiveGuess by regex("^#.*")
    val valueChar by regex("'(.{1}|\\\\.)'")
    val valueString by regex("\".*?\"")
    val valueBinary by regex("0b[0-1]+")
    val valueFloat by regex("[+-]?([0-9]+)([.][0-9]+)?f")
    val valueDouble by regex("[+-]?([0-9]+[.])[0-9]+")
    val valueLong by regex("[+-]?[0-9]+L")
    val valueInteger by regex("[+-]?[0-9]+")
    val valueBoolean by regex("(true|false)")
    val primitiveType by regex("(bool|int|float|double|char|void|wchar_t)")
    val primitiveTypeModifier by regex("(signed|unsigned|short|long)")
    val typeModifier by regex("(const|mutable|atomic)")
    val statementControl by regex("(if|else|switch|case)")
    val statementLoop by regex("(for|while|do|continue)")
    val statementTerminate by regex("(break|return)")
    val keywordIO by regex("(cout|cin)")
    val keyword by regex("(class|struct|enum|template|final|override|using|namespace|this)")
    val operatorGuess by regex("(sizeof|\\?:|->)")

    val operatorQualified by regex(":{2}")
    val operatorStream by regex("(<{2}|>{2})")
    val operatorRelational by regex("(==|!=|>|<|>=|<=)")
    val operatorLogical by regex("(&&|\\|\\||\\!)")
    val operatorArithmetic by regex("[/|%]")
    val operatorAssign by char('=')

    val asterisk by char('*')
    val ampersand by char('&')
    val tilda by char('~')
    val dot by char('.')
    val plus by char('+')
    val minus by char('-')

    val comma by char(',')
    val colon by char(':')
    val semicolon by char(';')

    val lb by char('(')
    val rb by char(')')
    val lcb by char('{')
    val rcb by char('}')
    val lsb by char('[')
    val rsb by char(']')

    val identifier by regex("[a-zA-Z][a-zA-Z0-9_]*")

    val newLine by char('\n', ignored = true)
    val whitespace by regex("\\s", ignored = true)


    fun parse(input: CharSequence) {
        val tokens = analyze(input)
        parseTokens(tokens)
    }

    fun parseTokens(tokens: List<Token>) {
        val tree = mutableListOf<ASTElement>()
        val stackIDK = Stack<ASTElement>()
        var savedTokens = mutableListOf<Token>()
        val bracketLevels = Stack<Pair<Token, List<Token>>>()

        for (currentToken in tokens) {
            savedTokens.add(currentToken)
            // Определяем корневой элемент последующего блока | currentTokenType is OpenBracket e.g. ([{
            val psi = TriePattern.findPsiElement(savedTokens)
            when (psi) {
                PsiElement.FUNCTION -> {
                    stackIDK.add(
                        ASTElement.BlockContent.Function(
                            definition = savedTokens.dropLast(1),
                            arguments = emptyList(),
                            content = emptyList()
                        )
                    )
                    savedTokens = mutableListOf()
                }

                else -> {
                    // ignore
                }
            }

            when (currentToken.text) {
                "if" -> {
                    stackIDK.add(
                        ASTElement.BlockContent.If(
                            token = currentToken,
                            condition = emptyList(),
                            content = emptyList()
                        )
                    )
                    savedTokens = mutableListOf()
                    continue
                }
                "while" -> {
                    stackIDK.add(
                        ASTElement.BlockContent.While(
                            token = currentToken,
                            condition = emptyList(),
                            content = emptyList()
                        )
                    )
                    savedTokens = mutableListOf()
                    continue
                }
                "else" -> {
                    stackIDK.add(
                        ASTElement.BlockContent.Else(
                            token = currentToken,
                            content = emptyList()
                        )
                    )
                    savedTokens = mutableListOf()
                    continue
                }
            }

            when {
                currentToken.isAnyOpenBracket -> {
                    savedTokens.remove(currentToken)
                    bracketLevels.push(currentToken to savedTokens)
                    savedTokens = mutableListOf()
                }

                currentToken.isAnyCloseBracket && bracketLevels.isNotEmpty() -> {
                    val currentBracketLevel = bracketLevels.pop()

                    val tokensBeforeLeftBracket = currentBracketLevel.second
                    val tokensBetweenBrackets = savedTokens.dropLast(1) // Отбрасываем currentToken

                    when (val lastAstItem = if (stackIDK.isEmpty()) null else stackIDK.peek()) {
                        is ASTElement.BlockContent.Function -> {
                            lastAstItem.apply {
                                if (!isArgumentsSet) {
                                    arguments = tokensBetweenBrackets
                                    isArgumentsSet = true
                                    savedTokens = mutableListOf()
                                } else {
                                    savedTokens = mutableListOf()
                                }
                            }
                        }

                        is ASTElement.BlockContent.While -> {
                            lastAstItem.apply {
                                if (!isConditionSet) {
                                    condition = tokensBetweenBrackets
                                    isConditionSet = true
                                    savedTokens = mutableListOf()
                                } else if (currentToken.type == rcb) {
                                    val currentStatement = stackIDK.pop()
                                    val rootAst = if (stackIDK.isEmpty()) null else stackIDK.peek()
                                    if (rootAst is ASTElement.BlockContent) {
                                        rootAst.content = buildList {
                                            addAll(rootAst.content)
                                            add(currentStatement)
                                        }
                                    } else throw RuntimeException("rootAst=$rootAst, items=$currentStatement")
                                } else {
                                    savedTokens.add(0, currentBracketLevel.first)
                                    savedTokens.addAll(0, tokensBeforeLeftBracket)
                                }
                            }
                        }
                        is ASTElement.BlockContent.If -> {
                            lastAstItem.apply {
                                if (!isConditionSet) {
                                    condition = tokensBetweenBrackets
                                    isConditionSet = true
                                    savedTokens = mutableListOf()
                                } else if (currentToken.type == rcb) {
                                    val currentStatement = stackIDK.pop()
                                    val rootAst = if (stackIDK.isEmpty()) null else stackIDK.peek()
                                    if (rootAst is ASTElement.BlockContent) {
                                        rootAst.content = buildList {
                                            addAll(rootAst.content)
                                            add(currentStatement)
                                        }
                                    } else throw RuntimeException("rootAst=$rootAst, items=$currentStatement")
                                } else {
                                    savedTokens.add(0, currentBracketLevel.first)
                                    savedTokens.addAll(0, tokensBeforeLeftBracket)
                                }
                            }
                        }

                        is ASTElement.BlockContent.Else -> {
                            if (currentToken.type == rcb) {
                                val currentStatement = stackIDK.pop()
                                val rootAst = if (stackIDK.isEmpty()) null else stackIDK.peek()
                                if (rootAst is ASTElement.BlockContent) {
                                    rootAst.content = buildList {
                                        addAll(rootAst.content)
                                        add(currentStatement)
                                    }
                                } else throw RuntimeException("rootAst=$rootAst, items=$currentStatement")
                            } else {
                                savedTokens.add(0, currentBracketLevel.first)
                                savedTokens.addAll(0, tokensBeforeLeftBracket)
                            }
                        }

                        is ASTElement.CodeLine -> {
                            throw RuntimeException("Сюда попадать не должно :D")
                        }

                        null -> TODO()
                    }
                }

                currentToken.type == semicolon -> {
                    val items = mutableListOf<ASTElement>()
                    var index = savedTokens.indexOfFirst { it.type == semicolon }
                    while (index != -1) {
                        val codeLine = ASTElement.CodeLine(savedTokens.subList(0, index + 1).toList())
                        items.add(codeLine)
                        savedTokens.removeAll(codeLine.content)
                        index = savedTokens.indexOfFirst { it.type == semicolon }
                    }
                    val rootAst = if (stackIDK.isEmpty()) null else stackIDK.peek()
                    if (rootAst is ASTElement.BlockContent) {
                        rootAst.content = buildList {
                            addAll(rootAst.content)
                            addAll(items)
                        }
                    } else throw RuntimeException("rootAst=$rootAst, items=$items")
                }
            }
        }
        println()
    }

    val Token.isAnyBracket: Boolean
        get() = (type == lb || type == rb) || (type == lcb || type == rcb)
    val Token.isAnyOpenBracket: Boolean
        get() = type == lb || type == lcb
    val Token.isAnyCloseBracket: Boolean
        get() = type == rb || type == rcb

    fun matchBrackets(typeA: TokenType, typeB: TokenType) = typeA == lb && typeB == rb || typeA == lcb && typeB == rcb
}

sealed interface ASTElement {

    data class CodeLine(val content: List<Token>) : ASTElement {
        override fun toString(): String {
            return content.joinToString(" ") { it.text }
        }
    }

    sealed interface BlockContent : ASTElement {
        var content: List<ASTElement>
        var isContentSet: Boolean

        data class While(
            var token: Token,
            var condition: List<Token>,
            override var content: List<ASTElement>,
            var isConditionSet: Boolean = false,
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${token.text} (${condition.joinToString("") { it.text }}) { $content }"
            }
        }

        data class If(
            var token: Token,
            var condition: List<Token>,
            override var content: List<ASTElement>,
            var isConditionSet: Boolean = false,
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${token.text} (${condition.joinToString("") { it.text }}) { $content }"
            }
        }

        data class Else(
            var token: Token,
            override var content: List<ASTElement>,
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${token.text} { $content }"
            }
        }

        data class Function(
            var definition: List<Token>,
            var arguments: List<Token>,
            override var content: List<ASTElement>,
            var isArgumentsSet: Boolean = false,
            override var isContentSet: Boolean = false,
        ) : BlockContent {
            override fun toString(): String {
                return "${definition.joinToString(" ") { it.text }} (${arguments.joinToString("") { it.text }}) { $content }"
            }
        }
    }
}


class AbstractSyntaxTree {
    private val root = Node()

    data class Node(
        var element: ASTElement? = null,
        val childNodes: MutableList<Node> = mutableListOf()
    )
}