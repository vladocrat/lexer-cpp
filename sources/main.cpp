Token::Token(Kind kind, const std::string& lexeme) {
    for(i = 0; i < 0; i++) {
        step = i * call();
    }
    while(i != 0) {
        println("text:while:start");
        double a = 433;
        if(true) {
            println("text:while-if");
        } else {
            if(false) {
                println("text:while-else-if");
            }
        }
        println("text:while:end");
    }
}

bool foo(const Token& other)
{
    singleCall();
    outerCall(nestedCall());
    outerCall(nestedCallWithArgs("simple-args", 12312));

    if (m_kind != other.kind) {
        for(i = 0; i < 0; i++) {
            step = i * call();
        }
        return false;
    }
    if (m_lexeme != other.lexeme) {
        return false;
    } else {
        var = 100;
        println("text-else");
    }

    return true;
}
//
//void bar(const Token& other)
//{
//    outerCall(FirstNestedCall(), SecondNestedCall()); Так делать не надо
//    if(call()); Так делать не надо
//    while(call()); Так делать не надо
//    for(call()); Так делать не надо
//}