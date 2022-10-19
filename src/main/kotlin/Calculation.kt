import java.util.*

object Calc {
    var calc = LinkedList<Token?>()
    fun makePoliz(input: Queue<Token?>): LinkedList<Token?> {
        while (!input.isEmpty()) {
            val token = input.peek()
            if (!(token!!.type === Lexem.WHILE || token!!.type === Lexem.IF)) {
                makePolizFromExpr(input)
            } else {
                makePolizFromWhile(input, token)
            }
        }
        return calc
    }

    private fun makePolizFromWhile(input: Queue<Token?>, tmp: Token?) {
        val boolExpr: Queue<Token?> = LinkedList()
        input.poll()
        var token = input.poll()
        val index = calc.size
        while (true) {
            assert(token != null)
            if (token!!.type === Lexem.LCB) break
            boolExpr.add(token)
            token = input.poll()
        }
        makePolizFromExpr(boolExpr)
        if (tmp!!.type === Lexem.WHILE) {
            calc.add(Token(Lexem.GOTO_INDEX, Integer.toString(p(calc.size, input))))
        }
        val p = Integer.toString(p(calc.size, input))
        if (tmp!!.type === Lexem.IF) {
            calc.add(Token(Lexem.GOTO_INDEX, p))
        }
        calc.add(Token(Lexem.GOTO, "!F"))
        val expr: Queue<Token?> = LinkedList()
        token = input.poll()
        while (true) {
            assert(token != null)
            if (token!!.type === Lexem.RCB) break
            if (token!!.type === Lexem.WHILE || token!!.type === Lexem.IF) {
                makePolizFromExpr(expr)
                makePolizFromWhile(input, token)
            }
            if (!(token!!.type === Lexem.WHILE || token!!.type === Lexem.IF)) expr.add(token)
            token = input.poll()
        }
        makePolizFromExpr(expr)
        if (tmp!!.type !== Lexem.IF) calc.add(Token(Lexem.GOTO_INDEX, Integer.toString(index)))
        if (tmp!!.type !== Lexem.WHILE) calc.add(Token(Lexem.GOTO_INDEX, p))
        calc.add(Token(Lexem.GOTO, "!"))
    }

    private fun p(size: Int, tokens: Queue<Token?>): Int {
        var p = size
        var i = 1
        val newtokens: Queue<Token?> = LinkedList(tokens)
        newtokens.poll()
        var newtoken: Token?
        while (i > 0) {
            newtoken = if (newtokens.isEmpty()) {
                break
            } else {
                newtokens.poll()
            }
            if (newtoken!!.type === Lexem.WHILE || newtoken!!.type === Lexem.IF) {
                i++
                p--
            }
            if (newtoken!!.type === Lexem.RCB) {
                i--
            }
            if (newtoken!!.type !== Lexem.C_OP) {
                if (!(newtoken!!.type === Lexem.INC || newtoken!!.type === Lexem.DEC)) {
                    p++
                } else p = p + 4
            }
        }
        p += 4
        return p
    }

    private fun makePolizFromExpr(input: Queue<Token?>) {
        val stack = Stack<Token?>()
        while (!input.isEmpty()) {
            var token = input.peek()
            if (token!!.type === Lexem.WHILE || token!!.type === Lexem.IF) {
                break
            }
            if (token!!.type === Lexem.TYPE_W) {
                stack.add(token)
            }
            if (token!!.type === Lexem.TYPE) {
                calc.add(token)
                calc.add(stack.pop())
            }
            token = input.poll()
            assert(token != null)
            if (token!!.type === Lexem.INC || token!!.type === Lexem.DEC) {
                calc.add(calc.last)
                var tmpToken = Token(Lexem.NUM, "1")
                calc.add(tmpToken)
                tmpToken = if (token!!.type === Lexem.INC) {
                    Token(Lexem.OP, "+")
                } else {
                    Token(Lexem.OP, "-")
                }
                calc.add(tmpToken)
                calc.add(Token(Lexem.ASSIGN_OP, "="))
            }

            //Если лексема является числом или переменной, добавляем ее в ПОЛИЗ-массив.
            if (token!!.type === Lexem.VAR || token!!.type === Lexem.NUM) {
                calc.add(token)
            }

            //Если лексема является бинарной операцией, тогда:
            if (token!!.type === Lexem.OP || token!!.type === Lexem.BOOL_OP || token!!.type === Lexem.ASSIGN_OP || token!!.type === Lexem.FUNC_OP) {
                if (!stack.empty()) {
                    while (getPriorOfOp(token!!.data) >= getPriorOfOp(stack.peek()!!.data)) {
                        calc.add(stack.pop())
                        if (stack.empty()) {
                            break
                        }
                    }
                }
                stack.push(token)
            }

            //Если лексема является открывающей скобкой, помещаем ее в стек.
            if (token!!.type === Lexem.LB) {
                stack.push(token)
            }
            if (token!!.type === Lexem.RB) {
                if (!stack.empty()) {
                    while (!stack.empty() && stack.peek()!!.type !== Lexem.LB) {
                        calc.add(stack.pop())
                    }
                    if (!stack.empty() && stack.peek()!!.type === Lexem.LB) {
                        stack.pop()
                    }
                }
            }
            if (token!!.type === Lexem.C_OP) {
                while (!stack.empty()) {
                    calc.add(stack.pop())
                }
            }
        }
        while (!stack.empty()) {
            calc.add(stack.pop())
        }
    }

    private fun getPriorOfOp(op: String): Int {
        return when (op) {
            "*", "/" -> 0
            "+", "-" -> 2
            ">", ">=", "<", "<=", "==", "!=" -> 3
            "=" -> 5
            else -> 4
        }
    }
}