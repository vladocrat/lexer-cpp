#include "lexer.h"

#include <algorithm>

Lexer::Lexer(const std::string& filePath, const std::string& writeFilePath)
{
    m_inputFileStream.open(filePath);

    if (!checkOpen(m_inputFileStream))
    {
        //throw new runtime exception
    }

    m_outputFileStream.open(writeFilePath);

    if (!checkOpen(m_outputFileStream))
    {
        //throw new runtime exception
    }
}

Lexer::~Lexer()
{
    if (!checkOpen(m_inputFileStream)) {
        //throw new runtime exception
    }

    m_inputFileStream.close();

    if (!checkOpen(m_outputFileStream))
    {
       //throw new runtime exception
    }

    m_outputFileStream.close();
}

void Lexer::parse()
{
    Token t;

    do {
        t = next();
        //std::cout << t << std::endl;
        m_outputFileStream << t << "\n";
    } while (t != Token(Token::Kind::EndOfFile, ""));
}

Token Lexer::next()
{
    char c;

    while (m_inputFileStream.get(c) && !m_inputFileStream.eof())
    {
        switch (c)
        {
        case '\n':
            return Token(Token::Kind::Separator, "\\n");
        case '\r':
            return Token(Token::Kind::Separator, "\\r");
        case '\t':
            return Token(Token::Kind::Separator, "\\t");
        case ' ':
            return Token(Token::Kind::Separator, "\' \'");
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
            return completeIdentifier(c);
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            return completeNumber(c);
        case '/':
            return commentToSpace();
        case '#':
            return completeDirective(c);
        case ';':
            return Token(Token::Kind::SemiColon, ";");
        case ':':
            return Token(Token::Kind::Colon, ":");
        case '(':
            return Token(Token::Kind::OpenedBrace, "(");
        case ')':
            return Token(Token::Kind::ClosedBrace, ")");
        case '<':
            return completeAngledBrace(c);
        case '>':
            return completeAngledBrace(c);
        case '{':
            return Token(Token::Kind::OpenedCurlyBrace, "{");
        case '}':
            return Token(Token::Kind::ClosedCurlyBrace, "}");
        case '\"':
            return completeStringLiteral(c);
        case '=':
            return Token(Token::Kind::Operator, "=");
        case '*':
            return Token(Token::Kind::Operator, "*");
        case '+':
            return Token(Token::Kind::Operator, "+");
        case '-':
            return Token(Token::Kind::Operator, "-");
        case '!':
            return Token(Token::Kind::Operator, "!");
        case '&':
            return Token(Token::Kind::Operator, "&");
        case '.':
            return Token(Token::Kind::Operator, ".");
        case ',':
            return Token(Token::Kind::Operator, ",");
        }
    }

    return Token(Token::Kind::EndOfFile, "");
}

Token Lexer::completeIdentifier(char lastChar)
{
    std::string finalString = "";
    finalString += lastChar;
    char c = lastChar;

    while ((isChar(c)) && isChar(m_inputFileStream.peek()))
    {
        m_inputFileStream.get(c);
        finalString += c;
    }

    if (isKeyword(finalString))
    {
        return Token(Token::Kind::Keyword, finalString);
    }

    return Token(Token::Kind::Identifier, finalString);
}

Token Lexer::completeNumber(char lastNumber)
{
    std::string finalString = "";
    finalString += lastNumber;
    char c = ' ';

    while (isNumber(c) && isNumber(m_inputFileStream.peek()))
    {
        m_inputFileStream.get(c);
        finalString += c;
    }

    m_inputFileStream.get(c);
    finalString += c;

    if (isKeyword(finalString))
    {
        return Token(Token::Kind::Keyword, finalString);
    }

    return Token(Token::Kind::Literal, finalString);
}

Token Lexer::commentToSpace()
{
    char c = ' ';

    while (m_inputFileStream.peek() != '\n')
    {
        m_inputFileStream.get(c);
    }

    m_inputFileStream.get(c);

    return Token(Token::Kind::Separator, "\\n");
}

Token Lexer::completeDirective(char lastChar)
{
    std::string finalString = "";
    finalString += lastChar;
    char c = ' ';

    while ((!m_inputFileStream.get(c) || !isSpace(c)) && isChar(c))
    {
        finalString += c;
    }

    return Token(Token::Kind::Directive, finalString);
}

Token Lexer::completeAngledBrace(char lastChar)
{
    std::string finalString = "";
    finalString += lastChar;
    char c = ' ';

    if (isDoubleAngledBrace(lastChar, m_inputFileStream.peek()))
    {
        m_inputFileStream.get(c);
        finalString += c;

        return Token(Token::Kind::Operator, finalString);
    }

    return Token(Token::Kind::Operator, finalString);
}

Token Lexer::completeStringLiteral(char lastChar)
{
    std::string finalString = "";
    finalString += lastChar;
    char c = ' ';

    while (m_inputFileStream.peek() != '\"')
    {
        m_inputFileStream.get(c);
        finalString += c;
    }

    m_inputFileStream.get(c);
    finalString += c;

    return Token(Token::Kind::Literal, finalString);
}

bool Lexer::isSpace(char c) const
{
    switch (c)
    {
    case '\n':
    case '\r':
    case '\t':
    case ' ':
        return true;
    }

    return false;
}

bool Lexer::isKeyword(const std::string& identifier) const
{
    auto it = std::find(begin(m_keywords), end(m_keywords), identifier);

    if (it != end(m_keywords))
    {
        return true;
    }

    return false;
}

bool Lexer::isNumber(char c) const
{
    switch (c)
    {
    case '0':
    case '1':
    case '2':
    case '3':
    case '4':
    case '5':
    case '6':
    case '7':
    case '8':
    case '9':
        return true;
    }

    return false;
}

bool Lexer::isChar(char c) const
{
    switch (c)
    {
    case '_':
    case 'a':
    case 'b':
    case 'c':
    case 'd':
    case 'e':
    case 'f':
    case 'g':
    case 'h':
    case 'i':
    case 'j':
    case 'k':
    case 'l':
    case 'm':
    case 'n':
    case 'o':
    case 'p':
    case 'q':
    case 'r':
    case 's':
    case 't':
    case 'u':
    case 'v':
    case 'w':
    case 'x':
    case 'y':
    case 'z':
    case 'A':
    case 'B':
    case 'C':
    case 'D':
    case 'E':
    case 'F':
    case 'G':
    case 'H':
    case 'I':
    case 'J':
    case 'K':
    case 'L':
    case 'M':
    case 'N':
    case 'O':
    case 'P':
    case 'Q':
    case 'R':
    case 'S':
    case 'T':
    case 'U':
    case 'V':
    case 'W':
    case 'X':
    case 'Y':
    case 'Z':
        return true;
    }

    return false;
}

bool Lexer::isDoubleAngledBrace(char currentChar, char nextChar) const
{
    return currentChar == nextChar;
}

template<class T>
bool Lexer::checkOpen(const T& stream) const
{
    if (!stream.is_open())
    {
        std::cout << "ERROR::FAILED_TO_OPEN_FILE" << std::endl;
        return false;
    }

    return true;
}
