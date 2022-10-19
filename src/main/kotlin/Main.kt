
fun main(args: Array<String>) {
    val rawFileAsString = LexFileReader.parseFile("C:\\Users\\Appleson\\Documents\\cpp-lexer\\sources\\main.cpp") ?: return
    val tokens = Lexer.tokenize(rawFileAsString)
    println("--------- Доступные токены: ------------")
    for (value in tokens) {
        println(value)
    }

//    try {
//        Parser.parse(tokens)
//    } catch (ex: Exception) {
//        System.err.println(ex)
//        System.exit(1)
//    }
//    println("-------------- ОПЗ: ------------")
//    val testCalc = Calc.makePoliz(tokens)
//    var i = 1
//    for (token in testCalc) {
//        println("$i $token")
//        i++
//    }
//    println("--------- Таблица переменных: -----------")
//    Calculation.calculate(testCalc)
}