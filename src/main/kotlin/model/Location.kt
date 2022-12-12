package model

/**
 * Модель описывает местоположение во входных данных
 *
 * @property row Номер строки, начиная с 1
 * @property column Номер столбца, начиная с 1
 * @property offset Количество символов перед местоположением
 */
data class Location(
    val row: Int,
    val column: Int,
    val offset: Int
) {
    companion object {
        /**
         * Represents a default location to be used when an actual location is unknown.
         */
        val Empty: Location = Location(0, 0, 0)
    }
}