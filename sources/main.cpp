#include <iostream>

#include "lexer.h"

int main()
{
    Lexer l("token.cpp", "newFile.txt");
    l.parse();

    return 0;
}
