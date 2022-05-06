package analyzer

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow

fun main(args: Array<String>) {
    if (args.size != 4) {
        println("Invalid number of args")
        return
    }

    if (args[0] !in listOf("--KMP", "--naive")) {
        println("First argument should be --KPM or --naive")
        return
    }

    val fileName = args[1]
    val P = args[2]
    val R = args[3]

    val bytes = Files.readAllBytes(Paths.get(fileName)).toList()
    val bytesP = P.toByteArray()


    val startTimeNano = System.nanoTime()

    val isMatch = if (args[0] == "--naive") naiveSearch( bytesP, bytes) else KMPSearch(bytesP,bytes)
    val endTimeNano = System.nanoTime()
    println(if (isMatch) R else "Unknown file type")
    val nanoToSec = 10.0
    val takenTimeSeconds = (endTimeNano - startTimeNano) / nanoToSec.pow(-6)
    println("It took $takenTimeSeconds seconds")


}

fun naiveSearch( bytesP: ByteArray, bytes: List<Byte>): Boolean {
//    var indexes = bytes.indexesOf(bytesP[0])
    var isMatch = false
    for (index in 0 .. bytes.lastIndex - bytesP.size+1) {
        isMatch = true
        for (n in 0..bytesP.lastIndex) {
            if ( bytesP[n] != bytes[index + n]) {
                isMatch = false
                break
            }
        }
        if (isMatch)
            break
    }
    return isMatch
}

fun KMPSearch( bytesP: ByteArray, bytes: List<Byte>): Boolean {
    val prefixFunction = prefix(bytesP)

    var isMatch = false
    var index=0

    while (index <= bytes.lastIndex - bytesP.size+1) {
        isMatch = true
        var matchCount =0
        for (n in 0..bytesP.lastIndex) {
            if (bytesP[n] != bytes[index + n]) {
                isMatch = false
                break
            }
            matchCount++
        }

        if (isMatch)
            break
        else{
            index+=matchCount-prefixFunction[matchCount-1]
        }
    }
    return isMatch
}
fun prefix( bytesP: ByteArray): MutableList<Int>{
    var lastCount =0
    val result = mutableListOf(lastCount)
    for (index in 1 .. bytesP.lastIndex){
        if (bytesP[index] == bytesP[lastCount]){
            lastCount++

        }else{
            lastCount=0
        }
        result.add(lastCount)
    }
    return result
}
//fun <E> Iterable<E>.indexesOf(e: E) = mapIndexedNotNull { index, elem -> index.takeIf { elem == e } }