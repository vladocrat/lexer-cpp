package lexer


sealed interface LogicExpression {
    data class Variable(val name: String) : LogicExpression
    data class Not(val expr: LogicExpression) : LogicExpression
    data class And(val left: LogicExpression, val right: LogicExpression) : LogicExpression

    data class Or(val left: LogicExpression, val right: LogicExpression) : LogicExpression
    // ==
    data class Eq(val left: LogicExpression, val right: LogicExpression) : LogicExpression
    // !=
    data class Ne(val left: LogicExpression, val right: LogicExpression) : LogicExpression

    // >
    data class Gt(val left: LogicExpression, val right: LogicExpression) : LogicExpression
    // <
    data class Lt(val left: LogicExpression, val right: LogicExpression) : LogicExpression
    // >=
    data class Ge(val left: LogicExpression, val right: LogicExpression) : LogicExpression
    // <=
    data class Le(val left: LogicExpression, val right: LogicExpression) : LogicExpression
}