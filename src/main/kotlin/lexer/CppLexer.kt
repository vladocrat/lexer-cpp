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
        parseBrackets(tokens)
    }

    fun parseBrackets(tokens: List<Token>) {
        val tree = mutableListOf<ASTElement>()
        val stackIDK = Stack<ASTElement>()
        var savedTokens = mutableListOf<Token>()
        val bracketLevels = Stack<Pair<Token, List<Token>>>()

        for (currentToken in tokens) {

            if (currentToken.isAnyBracket) {

                val isCurrentTokenClosesBracketBlock = bracketLevels.isNotEmpty()
                        && matchBrackets(bracketLevels.peek().first.type, currentToken.type)

                if (isCurrentTokenClosesBracketBlock) {
                    val (leftBracketToken, tokensBeforeLbt) = bracketLevels.pop()
                    val tokensBetweenBrackets = savedTokens

                    val conditionToCheck = buildList {
                        addAll(tokensBeforeLbt)
                        add(leftBracketToken)
                    }
                    val psiElement = TriePattern.findPsiElement(conditionToCheck)


                    when (psiElement) {
                        PsiElement.FUNCTION -> {
                            val astElement = ASTElement.Function(
                                definition = conditionToCheck.dropLast(1),
                                arguments = tokensBetweenBrackets
                            )
                            stackIDK.add(astElement)
                            println("conditionToCheck: ${conditionToCheck.joinToString("") { it.text }}")
                            println(psiElement)
                        }
                        else -> {
                            val tokenBeforeOpenBracket = tokensBeforeLbt.lastOrNull()
                            println(tokenBeforeOpenBracket)
                            when (tokenBeforeOpenBracket?.type) {
                                null -> {
                                    println("tokensBeforeLbt is empty")
                                }
                                statementLoop -> {
                                    if (tokenBeforeOpenBracket.text == "while") {
                                        val statement = ASTElement.WhileLoopStatement(
                                            token = tokenBeforeOpenBracket,
                                            condition = tokensBetweenBrackets
                                        )
                                        stackIDK.add(statement)
                                    }
                                }
                                statementControl -> {
                                    if (tokenBeforeOpenBracket.text == "if") { // Определение условия блока IF
                                        val statement = ASTElement.IfStatement(
                                            token = tokenBeforeOpenBracket,
                                            condition = tokensBetweenBrackets
                                        )
                                        stackIDK.add(statement)
                                    } else if (tokenBeforeOpenBracket.text == "else") { // Определение содержимого блока ELSE
                                        val lines = mutableListOf<ASTElement>()
                                        var line = mutableListOf<Token>()
                                        tokensBetweenBrackets.forEach {
                                            line.add(it)
                                            if (it.type == semicolon) {
                                                lines.add(ASTElement.Line(line))
                                                line = mutableListOf()
                                            }
                                        }
                                        tree.add(ASTElement.ElseStatement(tokenBeforeOpenBracket, ASTElement.Block(lines)))
                                    }
                                }
                                else -> { // Какие-то скобки, нужно смотреть в stackIDK
                                    if (!stackIDK.isEmpty()) when (stackIDK.peek()) {
                                        is ASTElement.IfStatement -> {
                                            val element = stackIDK.pop() as ASTElement.IfStatement

                                            val lines = mutableListOf<ASTElement>()
                                            var line = mutableListOf<Token>()
                                            tokensBetweenBrackets.forEach {
                                                line.add(it)
                                                if (it.type == semicolon) {
                                                    lines.add(ASTElement.Line(line))
                                                    line = mutableListOf()
                                                }
                                            }

                                            val newElem = element.copy(content = ASTElement.Block(lines))
                                            tree.add(newElem)
                                        }
                                        is ASTElement.WhileLoopStatement -> {
                                            val element = stackIDK.pop() as ASTElement.WhileLoopStatement

                                            val lines = mutableListOf<ASTElement>()
                                            var line = mutableListOf<Token>()
                                            tokensBetweenBrackets.forEach {
                                                line.add(it)
                                                if (it.type == semicolon) {
                                                    lines.add(ASTElement.Line(line))
                                                    line = mutableListOf()
                                                }
                                            }

                                            val newElem = element.copy(content = ASTElement.Block(lines))
                                            tree.add(newElem)
                                        }
                                        is ASTElement.Function -> {
                                            val element = stackIDK.pop() as ASTElement.Function

                                            val lines = mutableListOf<ASTElement>()
                                            var line = mutableListOf<Token>()
                                            tokensBetweenBrackets.forEach {
                                                line.add(it)
                                                if (it.type == semicolon) {
                                                    lines.add(ASTElement.Line(line))
                                                    line = mutableListOf()
                                                }
                                            }

                                            val newElem = element.copy(content = ASTElement.Block(lines))
                                            tree.add(newElem)
                                        }
                                        else -> {
                                            println("Unknown chain: ${conditionToCheck.joinToString("") { it.text }}")
                                        }
                                    }
                                }
                            }
//                            println("tokensBetweenBrackets: ${tokensBetweenBrackets.joinToString("") { it.text }}")
//                            println("tokensBeforeLbt: ${tokensBeforeLbt.joinToString("")}")
                            println()
                        }
                    }
                    savedTokens = tokensBeforeLbt.toMutableList().also { it.addAll(tokensBetweenBrackets) }
                } else {
                    bracketLevels.push(currentToken to savedTokens)
                    savedTokens = mutableListOf()
                }
            } else {
                savedTokens.add(currentToken)
                if (bracketLevels.isEmpty() && !TriePattern.startsWith(savedTokens)) {
                    println("Drop next tokens: ${savedTokens.joinToString(separator = "")}")
                    savedTokens.clear()
                }
            }
        }
        println(tree.joinToString("\n\n"))
    }

//    fun foo(tokens: List<Token>) {
//        if (TriePattern.startsWith(savedTokens)) {
//            val psiElement = TriePattern.findPsiElement(savedTokens)
//            if(psiElement != null) {
//            } else {
//                index++
//            }
//        } else {
//            index++
//        }
//    } else {
//        index++
//        savedTokens.clear()
//    }
//    }

    val Token.isAnyBracket: Boolean
        get() = (type == lb || type == rb) || (type == lcb || type == rcb)

    fun matchBrackets(typeA: TokenType, typeB: TokenType) = typeA == lb && typeB == rb || typeA == lcb && typeB == rcb
}

sealed interface ASTElement {

    data class Line(val content: List<Token>) : ASTElement {
        override fun toString(): String {
            return content.joinToString(" ") { it.text }
        }
    }

    data class Block(val content: List<ASTElement>) : ASTElement {
        override fun toString(): String {
            return content.joinToString("\n")
        }
    }

    data class IfStatement(
        val token: Token,
        val condition: List<Token>,
        val content: Block? = null
    ) : ASTElement {
        override fun toString(): String {
            return "IF (${condition.joinToString("") { it.text }}) { $content }"
        }
    }

    data class ElseStatement(
        val token: Token,
        val content: Block? = null
    ) : ASTElement {
        override fun toString(): String {
            return "ELSE { $content }"
        }
    }

    data class WhileLoopStatement(
        val token: Token,
        val condition: List<Token>,
        val content: Block? = null
    ) : ASTElement {
        override fun toString(): String {
            return "WHILE (${condition.joinToString("") { it.text }}) { $content }"
        }
    }

    data class Function(val definition: List<Token>, val arguments: List<Token>, val content: Block? = null) : ASTElement {
        override fun toString(): String {
            return "${definition.joinToString(" ") { it.text }} (${arguments.joinToString("") { it.text }}) { $content }"
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