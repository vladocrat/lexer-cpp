#ifndef TOKEN_H
#define TOKEN_H

#include <iostream>
#include <map>

class Token final
{
public:
    enum class Kind {
        Identifier,
        Operator,
        Keyword,
        Literal,
        Separator,
        Undentified,
        Directive,
        Colon,
        SemiColon,
        OpenedBrace,
        ClosedBrace,
        OpenedCurlyBrace,
        ClosedCurlyBrace,
        EndOfFile
    };

    Token() {}; //TODO deletelater
    Token(Kind kind, const std::string& lexeme);

    const Kind kind()                 const  { return m_kind; }
    const std::string lexeme()        const  { return m_lexeme; }
    const std::string kindStr()       const  { return m_kindStrings.find(m_kind)->second; }

    bool operator==(const Token&);
    bool operator!=(const Token&);
    friend std::ostream& operator<<(std::ostream& os, const Token& token);

private:
    std::map<Kind, std::string> m_kindStrings {
        { Kind::Identifier, "Identifier" },              { Kind::Operator, "Operator" },
        { Kind::Keyword, "Keyword" },                    { Kind::SemiColon, "Semicolon" },
        { Kind::Literal, "Literal" },                    { Kind::Separator, "Separator"},
        { Kind::Undentified, "Undentified" },            { Kind::EndOfFile, "EOF" },
        { Kind::Directive, "Directive" },                { Kind::Colon, "Colon" },
        { Kind::OpenedBrace, "OpenBrace" },              { Kind::OpenedBrace, "OpenBrace" },
        { Kind::ClosedBrace, "ClosedBrace" },            { Kind::ClosedBrace, "ClosedBrace" },
        { Kind::OpenedCurlyBrace, "OpenedCurlyBrace" },  { Kind::ClosedCurlyBrace, "ClosedCurlyBrace"},
    };

    Kind m_kind { Kind::Undentified };
    std::string m_lexeme { "" };
};

#endif // TOKEN_H
