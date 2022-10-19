import java.util.LinkedList
import kotlin.Throws
import java.lang.Exception

object Parser {
    private var token: Token? = null
    private var tokens: LinkedList<Token>? = null
    private var pos = 0

    fun parse(input: LinkedList<Token>) {
        tokens = LinkedList(input)
        while (pos < tokens!!.size) {
            expr(pos)
        }
    }

    private fun match() {
        token = tokens!![pos]
    }

    private fun expr(startPos: Int): Int {
        return try {
            whileExpr()
        } catch (ex: Exception) {
            try {
                pos = startPos
                ifExpr()
            } catch (e: Exception) {
                try {
                    pos = startPos
                    declarationExpr()
                } catch (ex1: Exception) {
                    try {
                        pos = startPos
                        funcExpr(pos)
                    } catch (ex2: Exception) {
                        pos = startPos
                        assignExpr()
                    }
                }
            }
        }
    }

    @Throws(Exception::class)
    private fun funcExpr(startPos: Int): Int {
        return try {
            pos = `var`(pos)
            pos = assignOp(pos)
            pos = `var`(pos)
            pos = method(pos)
            pos = func(pos)
            pos = lb(pos)
            pos = arExpr()
            pos = rb(pos)
            pos = cOp(pos)
            pos
        } catch (e: Exception) {
            pos = startPos
            pos = `var`(pos)
            pos = method(pos)
            pos = func(pos)
            pos = lb(pos)
            pos = arExpr()
            pos = rb(pos)
            pos = cOp(pos)
            pos
        }
    }

    @Throws(Exception::class)
    private fun method(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.METHOD) {
            throw Exception("Вместо METHOD найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun whileExpr(): Int {
        pos = whileW(pos)
        pos = term()
        pos = body()
        return pos
    }

    @Throws(Exception::class)
    private fun ifExpr(): Int {
        pos = ifW(pos)
        pos = term()
        pos = body()
        return pos
    }

    @Throws(Exception::class)
    private fun term(): Int {
        pos = lb(pos)
        pos = boolExpr(pos)
        pos = rb(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun boolExpr(position: Int): Int {
        var startPos = position
        try {
            pos = arExpr()
        } catch (ex: Exception) {
            pos = startPos
            pos = operand(pos)
        }
        pos = boolOp(pos)
        startPos = pos
        try {
            pos = arExpr()
        } catch (ex: Exception) {
            pos = startPos
            pos = operand(pos)
        }
        return pos
    }

    @Throws(Exception::class)
    private fun operand(startPos: Int): Int {
        return try {
            `var`(pos)
        } catch (ex: Exception) {
            try {
                pos = startPos
                num(pos)
            } catch (ex2: Exception) {
                pos = startPos
                bracketExpr()
            }
        }
    }

    @Throws(Exception::class)
    private fun bracketExpr(): Int {
        pos = lb(pos)
        pos = inBrackets(pos)
        pos = rb(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun inBrackets(startPos: Int): Int {
        try {
            pos = bracketExpr()
        } catch (ex: Exception) {
            pos = startPos
            pos = arExpr()
        }
        return pos
    }

    @Throws(Exception::class)
    private fun arExpr(): Int {
        pos = operand(pos)
        while (true) {
            try {
                pos = arOp(pos)
            } catch (e: Exception) {
                break
            }
            try {
                pos = operand(pos)
            } catch (e: Exception) {
                throw Exception(e.message)
            }
        }
        return pos
    }

    @Throws(Exception::class)
    private fun body(): Int {
        pos = lcb(pos)
        pos = bodyExpr()
        pos = rcb(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun bodyExpr(): Int {
        pos = expr(pos)
        var startPos = pos
        while (true) {
            try {
                pos = expr(pos)
                startPos = pos
            } catch (ex: Exception) {
                pos = startPos
                break
            }
        }
        return pos
    }

    @Throws(Exception::class)
    private fun assignExpr(): Int {
        pos = `var`(pos)
        var startPos = pos
        try {
            pos = assignOp(pos)
        } catch (ex: Exception) {
            pos = startPos
            pos = incAndDec(pos)
            pos = cOp(pos)
            return pos
        }
        startPos = pos
        try {
            pos = arExpr()
        } catch (ex: Exception) {
            pos = startPos
            pos = operand(pos)
        }
        pos = cOp(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun incAndDec(startPos: Int): Int {
        try {
            pos = inc(startPos)
        } catch (ex: Exception) {
            pos = startPos
            pos = dec(startPos)
        }
        return pos
    }

    @Throws(Exception::class)
    private fun declarationExpr(): Int {
        pos = `var`(pos)
        pos = typeW(pos)
        pos = type(pos)
        pos = cOp(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun whileW(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.WHILE) {
            throw Exception("Вместо while найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun ifW(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.IF) {
            throw Exception("Вместо if найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun typeW(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.TYPE_W) {
            throw Exception("Вместо new найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun type(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.TYPE) {
            throw Exception("Вместо type найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun lb(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.LB) {
            throw Exception("Вместо lb найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun rb(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.RB) {
            throw Exception("Вместо rb найдено " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun boolOp(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.BOOL_OP) {
            throw Exception("Вместо boolOp найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun func(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.FUNC_OP) {
            throw Exception("Вместо func найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun `var`(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.VAR) {
            throw Exception("Вместо var найдено: " + token!!.type + " " + token!!.data + " " + startPos)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun inc(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.INC) {
            throw Exception("Вместо inc найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun dec(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.DEC) {
            throw Exception("Вместо dec найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun num(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.NUM) {
            throw Exception("Вместо num найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun arOp(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.OP) {
            throw Exception("Вместо arOp найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun lcb(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.LCB) {
            throw Exception("Вместо lcb найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun rcb(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.RCB) {
            throw Exception("Вместо rcb найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun assignOp(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.ASSIGN_OP) {
            throw Exception("Вместо assignOp найдено: " + token!!.type + " " + token!!.data)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun cOp(startPos: Int): Int {
        match()
        if (token!!.type !== Lexem.C_OP) {
            throw Exception("Вместо cOp найдено: " + token!!.type + " " + token!!.data + " " + pos)
        }
        return startPos + 1
    }
}