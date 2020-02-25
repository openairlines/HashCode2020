package com.openairlines.hashcode

import org.apache.commons.math3.stat.Frequency
import java.io.File
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.*

fun main() {
    var startMs = System.currentTimeMillis()
    println(LocalDateTime.now())
    val files = File("inputs").listFiles()?.asList()
    files?.sortedBy { it.name }
    val score = files?.filter { it.name != "d.txt" }
            ?.map {
                println(it.name)
                processFile(it)
            }?.sum()!!
    if (score > 24952686)
        println("NEW SCORE")
    var endMs = System.currentTimeMillis()
    println(LocalDateTime.now().toString() + " (${(endMs - startMs) / 1000}s)")
    println("Total ${DecimalFormat.getInstance(Locale.ROOT).format(score)}")
}

fun processFile(file: File): Int {
    // Read input
    val lines = file.readLines()
    val totalNumBooks = lines[0].split(" ")[0].toInt()
    val totalDays = lines[0].split(" ")[2].toInt()
    val allBookScores = lines[1].split(" ").map { it.toInt() }
    val totalBookScores = allBookScores.sum()
    val uniqueBookScore = if (allBookScores.distinct().count() == 1) allBookScores.first() else null

    val libraries = emptyList<Library>().toMutableList()
    for ((idLib, i) in (2 until lines.size - 1 step 2).withIndex()) {
        val elements = lines[i].split(" ")
        val books = lines[i + 1].split(" ").map {
            Book(it.toInt(), allBookScores[it.toInt()])
        }
        libraries.add(Library(idLib, elements[1].toInt(), elements[2].toInt(), books.sortedByDescending { it.score }.toMutableList()))
    }
    val totalLibs = libraries.size

    val booksFreq = Frequency()
    allBookScores.forEach { booksFreq.addValue(it) }
    println("Libraries: ${libraries.size}" +
            ", avg score: ${libraries.map { it to it.books.sumBy { it.score } }.sumBy { it.second } / totalLibs}" +
            ", avg books: ${libraries.map { it.books.count() }.sum() / totalLibs}" +
            ", avg signup: ${libraries.sumBy { it.signupTime } / totalLibs}")
    println("Books score")
    if (uniqueBookScore != null)
        println("Unique: $uniqueBookScore")
    else
        println("min: ${allBookScores.min()}" +
                ", max: ${allBookScores.max()}" +
                ", avg: ${allBookScores.average()}" +
                ", med: ${allBookScores[allBookScores.size / 2]}" +
                ", mode: ${booksFreq.mode} (*${booksFreq.getCount(booksFreq.mode.firstOrNull())})")

    // Start optimizing...
    val outputFileLines = listOf<String>().toMutableList()
    var remainingDays = totalDays
    var nbLibApplied = 0
    val scannedBooks = mutableSetOf<Book>()
    val uniqueScannedBooks = mutableSetOf<Book>()

    while (remainingDays > 0 && libraries.isNotEmpty()) {
        if (remainingDays.rem(1000) <= 1) print("$remainingDays, ")
        libraries.removeIf {
            it.signupTime >= remainingDays ||
                    uniqueScannedBooks.containsAll(it.books)
//                    numberOfNewBookUntil(it, remainingDays, scannedBooks) <= 0
        }
        val scoredLibs = if (uniqueBookScore == null) libraries
                .map { it to newBookUntil(it, remainingDays, scannedBooks) }
                //.sortedBy { it.second.sumBy { it.score } / it.first.signupTime }
                //.sortedWith(compareBy<Pair<Library, List<Book>>> { it.second.sumBy { it.score } / it.first.signupTime }.then(compareBy { it.second.sumBy { it.score } / remainingDays }))
                .sortedBy { it.second.sumBy { it.score } / it.first.signupTime }
                .takeLast(maxOf(1, libraries.size / 90))
                .sortedBy { it.second.sumBy { it.score } / remainingDays }
                .sortedBy { it.second.size }
        //.sortedWith(compareBy<Pair<Library, List<Book>>> { it.second.sumBy { it.score } / it.first.signupTime }.then(compareBy { it.first.signupTime }))
        else libraries
                .map { it to newBookUntil(it, remainingDays, scannedBooks) }
                .sortedWith(compareByDescending<Pair<Library, List<Book>>> { it.first.signupTime }.then(compareBy<Pair<Library, List<Book>>> { it.second.size }))
                .takeLast(maxOf(1, libraries.size / 90))
                //.sortedBy { it.second.sumBy { it.score } / remainingDays }
                .sortedBy { it.second.size }

        scoredLibs.lastOrNull()?.let {
            val library = it.first
            libraries.remove(library)
            val newBooks = it.second
            if (newBooks.isNotEmpty()) {
                uniqueScannedBooks.addAll(newBooks)
                if (uniqueBookScore != null) {
                    scannedBooks.addAll(newBooks)
                }
                // Yeah, one more library applied, go to next lib
                nbLibApplied++
                remainingDays -= library.signupTime
                // Output file content generation
                outputFileLines.add("${library.id} ${newBooks.size}")
                outputFileLines.add(newBooks.joinToString(" ") { book -> book.id.toString() })
            }
        }

    }
    println()

// Write results
    File("outputs").mkdir()
    val outPutFile = File("outputs", file.name)
    outPutFile.delete()
    outputFileLines.add(0, nbLibApplied.toString())
    outputFileLines.forEach {
        outPutFile.appendText("$it \n")
    }

    val sentScore = uniqueScannedBooks.sumBy { it.score }
    val sentBooks = uniqueScannedBooks.size
    println("${outPutFile.name}: totalDays: $totalDays, score: $sentScore/$totalBookScores, books: $sentBooks/$totalNumBooks, libs: $nbLibApplied/$totalLibs")
    return sentScore
}


fun scoreForNewBookUntil(library: Library, deadline: Int, alreadyScanBooks: Set<Book>): Int {
    val numOfNewBooks = numberOfNewBookUntil(library, deadline, alreadyScanBooks).toInt()
    return if (numOfNewBooks <= 0)
        0
    else
        library.books.filter { !alreadyScanBooks.contains(it) }.subList(0, numOfNewBooks).sumBy { it.score }

}

fun numberOfNewBookUntil(library: Library, deadline: Int, alreadyScanBooks: Set<Book>): Int {
    return if (library.signupTime >= deadline)
        0
    else
        kotlin.math.min((deadline - library.signupTime).toDouble() * library.shipPerDay,
                library.books.filter { !alreadyScanBooks.contains(it) }.size.toDouble()).toInt()
}

fun newBookUntil(library: Library, deadline: Int, alreadyScanBooks: Set<Book>): List<Book> {
    val newBooks = library.books.filter { !alreadyScanBooks.contains(it) }
    return if (library.signupTime >= deadline)
        emptyList()
    else {
        if ((deadline - library.signupTime).toDouble() * library.shipPerDay >= newBooks.size)
            newBooks else
            newBooks.subList(0, (deadline - library.signupTime) * library.shipPerDay)
    }
}


data class Book(val id: Int, val score: Int)

data class Library(val id: Int, val signupTime: Int, val shipPerDay: Int, val books: MutableList<Book>)