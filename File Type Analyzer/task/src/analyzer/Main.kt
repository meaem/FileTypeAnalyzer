package analyzer

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow

fun main(args:Array<String>) {
    if (args.size != 4){
        println("Invalid number of args")
    return
    }

    if (args[0] !in listOf("--KMP","--naive")){
        println("First argument should be --KPM or --naive")
        return
    }

    val fileName = args[1]
    val P = args[2]
    val R = args[3]

    val bytes = Files.readAllBytes(Paths.get(fileName)).toList()
    val bytesP = P.toByteArray()

    var indexes =bytes.indexesOf(bytesP[0])
val startTimeNano = System.nanoTime()

    val isMatch = naiveSearch(indexes,bytesP,bytes)
    val endTimeNano = System.nanoTime()
    println(if(isMatch) R else "Unknown file type")
    val nanoToSec = 10.0
    val takenTimeSeconds = (endTimeNano - startTimeNano) / nanoToSec.pow(-6)
    println("It took $takenTimeSeconds seconds")


}
fun naiveSearch(indexes: List<Int>, bytesP: ByteArray, bytes: List<Byte>): Boolean {
    var isMatch=false
    for (index in indexes){
        isMatch = true
        for ( n in 1 .. bytesP.lastIndex){
            if (n +index > bytes.lastIndex || bytesP[n] != bytes[index+n]){
                isMatch = false
                break
            }
        }
        if (isMatch)
            break
    }
    return isMatch
}

fun <E> Iterable<E>.indexesOf(e: E)
        = mapIndexedNotNull{ index, elem -> index.takeIf{ elem == e } }