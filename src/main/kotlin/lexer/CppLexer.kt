package lexer

import tokenizer.FirstMatchTokenizer
import tokenizer.TokenizerProvider
import types.char
import types.regex

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
}