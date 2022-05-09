package analyzer

import java.io.File
import java.nio.file.Files
import kotlin.concurrent.thread

data class FileTypePattern(val priority: Int, val pattern: String, val fileType: String){
    override fun toString(): String {
        return "'$priority', '$pattern', '$fileType'"
    }
}

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Invalid number of args")
        return
    }


    val folderName = args[0]
    val dbFile = args[1]

    val patterns = readPatternDBFile(dbFile).sortedByDescending { it.priority }

    val files = File(folderName).listFiles()
    val matches = MutableList(files!!.size) { -1 }
    val patternsByts = patterns.map { it.pattern.toByteArray() }


    for (f in files.withIndex()) {
        thread {
            matches[f.index] = checkFile(f.value, patternsByts)
            val type = if (matches[f.index] != -1) patterns[matches[f.index]].fileType else "Unknown file type"
            println("${f.value.name}: $type")
        }.join()

    }
    println(patterns.joinToString("\n"))
}

fun readPatternDBFile(dbFile: String): List<FileTypePattern> {
    val list = mutableListOf<FileTypePattern>()
    for (line in File(dbFile).readLines()) {
        val tokens = line.split(";").map { it.trim('"') }
        list.add(FileTypePattern(tokens[0].toInt(), tokens[1], tokens[2]))
    }
    return list
}

fun checkFile(file: File, patternsBytes: List<ByteArray>): Int {
    val bytes = Files.readAllBytes(file.toPath()).toList()
    var index = -1
    for (bytesP in patternsBytes.withIndex()) {
        if (RKSearch(bytesP.value, bytes)) {
            index = bytesP.index
            break
        }
    }
    return index
}




fun RKSearch(bytesP: ByteArray, bytes: List<Byte>): Boolean {
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
//
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