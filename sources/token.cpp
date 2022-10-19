#include "token.h"

#include <algorithm>
#include <iomanip>

Token::Token(Kind kind, const std::string& lexeme) : m_kind(kind), m_lexeme(lexeme)
{

}

bool Token::operator==(const Token& other)
{
    if (m_kind != other.kind()) return false;
    if (m_lexeme != other.lexeme()) return false;

    return true;
}

bool Token::operator!=(const Token& other)
{
    return !(*this == other);
}

std::ostream& operator<<(std::ostream& os, const Token& token)
{
    return os << token.kindStr() << std::setw(15) << "|" << token.lexeme() << "|";
}
