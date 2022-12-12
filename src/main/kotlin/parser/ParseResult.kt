package parser

import model.Location
import model.Token
import types.EofTokenType
import types.TokenType
import java.lang.RuntimeException

/**
 * Result of parsing with type [T].
 */
sealed interface ParseResult<out T>

/**
 * Represents a success while parsing.
 * Common usage: when you get [ParseResult], check whether it is [SuccessfulParse] or [ParseFailure].
 */
interface SuccessfulParse<out T> : ParseResult<T> {
    /**
     * Directly the result of parsing.
     */
    val value: T

    /**
     * Next token index to parse after this success.
     */
    val nextTokenIndex: Int
}

/**
 * Default successful result.
 */
data class ParsedValue<out T>(
    override val value: T,
    override val nextTokenIndex: Int
) : SuccessfulParse<T>

/**
 * Represents a failure while parsing.
 * Common usage: when you get [ParseResult], check whether it is [SuccessfulParse] or [ParseFailure].
 */
interface ParseFailure : ParseResult<Nothing> {
    /**
     * Diagnostic message in order to understand why parsing failed.
     * You must provide it!
     */
    val message: String
}

/**
 * Represents a failure while parsing with knowing exact location where it failed.
 */
interface LocatedParseFailure : ParseFailure {
    /**
     * Directly the location of failure.
     */
    val location: Location
}

/**
 * Represents a failure at [location] when a parser expected token [expected] but got [actual].
 */
data class MismatchTokenTypeFailure(
    override val location: Location,
    private val expected: TokenType,
    private val actual: TokenType
) : LocatedParseFailure {
    override val message: String
        get() = "Token mismatch at $location: expected=$expected, actual=$actual."
}

/**
 * Represents a failure at [location] when a parser expected token [expected] but got EOF.
 */
fun unexpectedEofFailure(location: Location, expected: TokenType): LocatedParseFailure {
    return MismatchTokenTypeFailure(location, expected, EofTokenType)
}

data class NoSuchAlternativeFailure(
    override val location: Location,
    private val alternativeFailures: List<ParseFailure>
) : LocatedParseFailure {
    // TODO nicer message
    override val message: String
        get() = "No such appropriate alternative at $location:\n" +
                alternativeFailures.joinToString("\n") { it.message }
}

data class UnparsedRemainderFailure(
    private val nextToken: Token
) : LocatedParseFailure {
    override val location: Location
        get() = nextToken.location

    override val message: String
        get() = "The parser matched only the part of the input. " +
                "Stopped at $location on the token named ${nextToken.type.name}."
}

fun ParseFailure.toException() = RuntimeException("suck")
