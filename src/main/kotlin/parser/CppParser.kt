package parser

import error.ParseException
import lexer.CppLexer
import model.Token
import java.util.*

typealias LeftBracket = Token
typealias PreviousTokensBeforeLeftBracket = List<Token>

object CppParser {

    private val Token.isAnyOpenBracket: Boolean get() = type == CppLexer.lb || type == CppLexer.lcb
    private val Token.isAnyCloseBracket: Boolean get() = type == CppLexer.rb || type == CppLexer.rcb

    fun parse(tokens: List<Token>): List<ASTElement> {
        val resultStack = Stack<ASTElement>()
        val bracketLevels = Stack<Pair<LeftBracket, PreviousTokensBeforeLeftBracket>>()
        var previousTokens = mutableListOf<Token>()


        for (currentToken in tokens) {

            // Определяем корневой элемент последующего блока | currentTokenType is OpenBracket e.g. ([{
            val tokensChain = buildList {
                addAll(previousTokens)
                add(currentToken)
            }
            when (Trie.findPsiElement(tokensChain)) {
                PsiElement.FUNCTION -> {
                    // Определение функции
                    resultStack.add(
                        ASTElement.BlockContent.Function(
                            definition = previousTokens.toList(),
                            arguments = emptyList(),
                            content = emptyList()
                        )
                    )
                    previousTokens = mutableListOf()
                }

                PsiElement.FUN_CALL -> {
                    // Определение вызова функции
                    resultStack.add(
                        ASTElement.FunctionCall(
                            content = previousTokens.toList()
                        )
                    )
                    previousTokens = mutableListOf()
                }

                else -> {
                    //ignore
                    println("PsiElement not found. Unknown tokens chain: $tokensChain")
                }
            }

            // Попытка определить блоковый элемент [ASTElement.BlockContent]
            when (currentToken.text) {
                CppLexer.FOR -> {
                    resultStack.add(
                        ASTElement.BlockContent.For(
                            token = currentToken,
                            definition = null,
                            content = emptyList()
                        )
                    )
                    previousTokens = mutableListOf()
                    continue
                }

                CppLexer.WHILE -> {
                    resultStack.add(
                        ASTElement.BlockContent.While(
                            token = currentToken,
                            condition = emptyList(),
                            content = emptyList()
                        )
                    )
                    previousTokens = mutableListOf()
                    continue
                }

                CppLexer.IF -> {
                    resultStack.add(
                        ASTElement.BlockContent.If(
                            token = currentToken,
                            condition = emptyList(),
                            content = emptyList()
                        )
                    )
                    previousTokens = mutableListOf()
                    continue
                }

                CppLexer.ELSE -> {
                    resultStack.add(
                        ASTElement.BlockContent.Else(
                            token = currentToken,
                            content = emptyList()
                        )
                    )
                    previousTokens = mutableListOf()
                    continue
                }

                else -> {
                    //ignore
                    println("BlockContent not found")
                }
            }

            when {
                // Обработка открытия скобки
                currentToken.isAnyOpenBracket -> {
                    bracketLevels.push(currentToken to previousTokens.toList())
                    previousTokens = mutableListOf()
                }
                // Обработка закрытия скобки
                currentToken.isAnyCloseBracket && bracketLevels.isNotEmpty() -> {
                    val (leftBracket, tokensBeforeLeftBracket) = bracketLevels.pop()

                    val rootAstElement = if (resultStack.isEmpty()) null else resultStack.peek()

                    when (rootAstElement) {
                        is ASTElement.FunctionCall -> {
                            val item = resultStack.pop() as ASTElement.FunctionCall
                            // Определение вызова функции
                            val functionCall = buildList {
                                addAll(previousTokens)
                                add(currentToken)
                                add(0, leftBracket)
                                addAll(0, item.content)
                                addAll(0, tokensBeforeLeftBracket)
                            }.toMutableList()
                            previousTokens = functionCall
                        }

                        is ASTElement.BlockContent.For -> {
                            with(rootAstElement) {
                                if (definition == null) {
                                    // Определение цикла: for(params)
                                    definition = buildList {
                                        addAll(content.flatMap { (it as? ASTElement.CodeLine)?.content ?: emptyList() })
                                        addAll(previousTokens) //previousTokens = все что находится между скобок
                                    }
                                    content = emptyList()
                                    previousTokens = mutableListOf()
                                } else if (currentToken.type == CppLexer.rcb) {
                                    // Обработка скобки закрывающей блок For
                                    val currentStatement = resultStack.pop()
                                    val rootAst = if (resultStack.isEmpty()) null else resultStack.peek()
                                    if (rootAst is ASTElement.BlockContent) {
                                        rootAst.content = buildList {
                                            addAll(rootAst.content)
                                            add(currentStatement)
                                        }
                                    } else throw RuntimeException("rootAst=$rootAst, items=$currentStatement")
                                } else {
                                    // Сохраняем токены для последующей обработки, недостаточно данных
                                    previousTokens.add(0, leftBracket)
                                    previousTokens.addAll(0, tokensBeforeLeftBracket)
                                }
                            }
                        }

                        is ASTElement.BlockContent.Function -> {
                            with(rootAstElement) {
                                if (!isArgumentsSet) {
                                    // Определение аргументов функции
                                    //previousTokens = все что находится между скобок
                                    arguments = previousTokens
                                    isArgumentsSet = true
                                    previousTokens = mutableListOf()
                                } else {
                                    previousTokens = mutableListOf()
                                }
                            }
                        }

                        is ASTElement.BlockContent.While -> {
                            with(rootAstElement) {
                                if (!isConditionSet) {
                                    // Определение условия цикла While
                                    //previousTokens = все что находится между скобок
                                    condition = previousTokens
                                    isConditionSet = true
                                    previousTokens = mutableListOf()
                                } else if (currentToken.type == CppLexer.rcb) {
                                    // Обработка скобки закрывающей блок While
                                    val currentStatement = resultStack.pop()
                                    val rootAst = if (resultStack.isEmpty()) null else resultStack.peek()
                                    if (rootAst is ASTElement.BlockContent) {
                                        rootAst.content = buildList {
                                            addAll(rootAst.content)
                                            add(currentStatement)
                                        }
                                    } else throw RuntimeException("rootAst=$rootAst, items=$currentStatement")
                                } else {
                                    // Сохраняем токены для последующей обработки, недостаточно данных
                                    previousTokens.add(0, leftBracket)
                                    previousTokens.addAll(0, tokensBeforeLeftBracket)
                                }
                            }
                        }

                        is ASTElement.BlockContent.If -> {
                            with(rootAstElement) {
                                if (!isConditionSet) {
                                    // Определение условия
                                    //previousTokens = все что находится между скобок
                                    condition = previousTokens
                                    isConditionSet = true
                                    previousTokens = mutableListOf()
                                } else if (currentToken.type == CppLexer.rcb) {
                                    // Обработка скобки закрывающей блок IF
                                    val currentStatement = resultStack.pop()
                                    val rootAst = if (resultStack.isEmpty()) null else resultStack.peek()
                                    if (rootAst is ASTElement.BlockContent) {
                                        rootAst.content = buildList {
                                            addAll(rootAst.content)
                                            add(currentStatement)
                                        }
                                    } else throw ParseException("rootAst=$rootAst, items=$currentStatement")
                                } else {
                                    // Сохраняем токены для последующей обработки, недостаточно данных
                                    previousTokens.add(0, leftBracket)
                                    previousTokens.addAll(0, tokensBeforeLeftBracket)
                                }
                            }
                        }

                        is ASTElement.BlockContent.Else -> {
                            if (currentToken.type == CppLexer.rcb) {
                                // Обработка скобки закрывающей блок Else
                                val currentStatement = resultStack.pop()
                                val rootAst = if (resultStack.isEmpty()) null else resultStack.peek()
                                if (rootAst is ASTElement.BlockContent) {
                                    rootAst.content = buildList {
                                        addAll(rootAst.content)
                                        add(currentStatement)
                                    }
                                } else throw ParseException("rootAst=$rootAst, items=$currentStatement")
                            } else {
                                // Сохраняем токены для последующей обработки, недостаточно данных
                                previousTokens.add(0, leftBracket)
                                previousTokens.addAll(0, tokensBeforeLeftBracket)
                            }
                        }

                        else -> throw ParseException("BTOOM!!!")
                    }

                }

                // Обработка ; - логически завершенного вызова
                currentToken.type == CppLexer.semicolon -> {
                    val items = mutableListOf<ASTElement>()

                    // Формирование списка [ASTElement.CodeLine] из ранее сохраненных токенов [previousTokens]
                    previousTokens.add(currentToken)
                    var index = previousTokens.indexOfFirst { it.type == CppLexer.semicolon }
                    while (index != -1) {
                        val codeLine = ASTElement.CodeLine(previousTokens.subList(0, index + 1).toList())
                        items.add(codeLine)
                        previousTokens.removeAll(codeLine.content)
                        index = previousTokens.indexOfFirst { it.type == CppLexer.semicolon }
                    }

                    // Привязка списка [ASTElement.CodeLine] к родительскому блоку кода
                    val rootAst = if (resultStack.isEmpty()) null else resultStack.peek()
                    if (rootAst is ASTElement.BlockContent) {
                        rootAst.content = buildList {
                            addAll(rootAst.content)
                            addAll(items)
                        }
                    } else throw ParseException("rootAst=$rootAst, items=$items")
                }

                else -> {
                    previousTokens.add(currentToken)
                }
            }
        }

        return resultStack.toList().reversed()
    }
}