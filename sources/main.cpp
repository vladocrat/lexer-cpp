#include <iostream>

#include "lexer.h"

int main(char* argc, char** argv) {
  double a = 0.0;
  while (a != 20)
  {
    if (a < 10)
    {
      a *= 1.2;
    } else
        a += 1;

  }

  return 0;
}