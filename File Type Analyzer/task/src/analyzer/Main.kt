package analyzer

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args:Array<String>) {

    if (args.size != 3){
        println("Invalid number of args0")
    return
    }

    val fileName = args[0];
    val P = args[1]
    val R = args[2]

    val bytes = Files.readAllBytes(Paths.get(fileName)).toList()
    val bytesP = P.toByteArray()
    var isMatch=false
    var indexes =bytes.indexesOf(bytesP[0])

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
    println(if(isMatch) R else "Unknown file type")


}

fun <E> Iterable<E>.indexesOf(e: E)
        = mapIndexedNotNull{ index, elem -> index.takeIf{ elem == e } }