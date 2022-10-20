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
)