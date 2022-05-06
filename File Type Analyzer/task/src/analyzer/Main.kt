package analyzer

import java.io.File
import java.nio.file.Files
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Invalid number of args")
        return
    }


    val folderName = args[0]
    val P = args[1]
    val R = args[2]

    val files = File(folderName).listFiles()
    val matches = MutableList(files!!.size) { false }
    val bytesP = P.toByteArray()
//    val startTimeNano = System.nanoTime()

//    val isMatch = checkFile(fn,bytesP)
//    val endTimeNano = System.nanoTime()

//    val nanoToSec = 10.0
//    val takenTimeSeconds = (endTimeNano - startTimeNano) / nanoToSec.pow(-6)
//    println("It took $takenTimeSeconds seconds")

    for (f in files.withIndex()) {
        thread {

            matches[f.index] = checkFile(f.value, bytesP)
            val type=if (matches[f.index]) R else "Unknown file type"
            println("${f.value.name}: $type")
        }.join()

    }
}

fun checkFile(file: File, bytesP: ByteArray): Boolean {
    val bytes = Files.readAllBytes(file.toPath()).toList()
    return KMPSearch(bytesP, bytes)
}
//fun naiveSearch( bytesP: ByteArray, bytes: List<Byte>): Boolean {
////    var indexes = bytes.indexesOf(bytesP[0])
//    var isMatch = false
//    for (index in 0 .. bytes.lastIndex - bytesP.size+1) {
//        isMatch = true
//        for (n in 0..bytesP.lastIndex) {
//            if ( bytesP[n] != bytes[index + n]) {
//                isMatch = false
//                break
//            }
//        }
//        if (isMatch)
//            break
//    }
//    return isMatch
//}

fun KMPSearch(bytesP: ByteArray, bytes: List<Byte>): Boolean {
    val prefixFunction = prefix(bytesP)

    var isMatch = false
    var index = 0

    while (index <= bytes.lastIndex - bytesP.size + 1) {
        isMatch = true
        var matchCount = 0
        for (n in 0..bytesP.lastIndex) {
            if (bytesP[n] != bytes[index + n]) {
                isMatch = false
                break
            }
            matchCount++
        }

        if (isMatch)
            break
        else {
            index += if (matchCount == 0) 1 else matchCount - prefixFunction[matchCount - 1]
        }
    }
    return isMatch
}

fun prefix(bytesP: ByteArray): MutableList<Int> {
    var lastCount = 0
    val result = mutableListOf(lastCount)
    for (index in 1..bytesP.lastIndex) {
        if (bytesP[index] == bytesP[lastCount]) {
            lastCount++

        } else {
            lastCount = 0
        }
        result.add(lastCount)
    }
    return result
}
//fun <E> Iterable<E>.indexesOf(e: E) = mapIndexedNotNull { index, elem -> index.takeIf { elem == e } }