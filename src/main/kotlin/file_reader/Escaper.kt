package file_reader

import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader

class Escaper {
    fun readFile(fileName: String): String? {
        var text: String? = null
        try {
            val br = BufferedReader(FileReader(fileName))
            val sb = StringBuilder()
            br.lines().forEach { s: String? ->
                sb.append(s).append('\n')
            }
            text = buildUnescapedStr(sb.toString())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return text
    }

    private fun buildUnescapedStr(line: String): String {
        val sb = StringBuilder()
        line.chars()
            .mapToObj { i: Int -> i.toChar().toString() }
            .map { character: String? ->
                if (character == "$") "\\$" else character
            }
            .forEach { str: String? -> sb.append(str) }
        return sb.toString()
    }
}