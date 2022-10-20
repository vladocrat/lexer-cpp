package model

data class Location(

    val offset: Int,

    val row: Int,

    val column: Int
) {
    override fun toString(): String = "loc(row=$row, column=$column, offset=$offset)"
}