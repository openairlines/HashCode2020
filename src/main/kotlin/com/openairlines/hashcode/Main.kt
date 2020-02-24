package com.openairlines.hashcode

import org.apache.commons.math3.stat.Frequency
import java.io.File
import java.lang.Integer.min
import java.time.LocalDateTime

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
    println(LocalDateTime.now().toString() + "(${(endMs - startMs) / 1000}s)")
    println("Total $score")
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
    val scannedBooks = mutableSetOf<Int>()
    var sentScore = 0
    var sentBooks = 0

    while (remainingDays > 0 && libraries.isNotEmpty()) {
        libraries.removeIf {
            it.signupTime >= remainingDays ||
                    scannedBooks.containsAll(it.books.map { book -> book.id }) ||
                    numberOfNewBookUntil(it, remainingDays, scannedBooks) <= 0
        }
        libraries.sortByDescending { scoreForNewBookUntil(it, remainingDays, scannedBooks) / it.signupTime }
        libraries.firstOrNull()?.let { library ->
            libraries.remove(library)
            val newBooks = newBookUntil(library, remainingDays, scannedBooks)
            if (newBooks.isNotEmpty()) {
                // Just some indicators...
                sentScore += newBooks.sumBy { it.score }
                sentBooks += newBooks.size
                // Keep track of already scanned books
                //scannedBooks.addAll(newBooks.map { it.id })
                // Yeah, one more library applied, go to next lib
                nbLibApplied++
                remainingDays -= library.signupTime
                // Output file content generation
                outputFileLines.add("${library.id} ${newBooks.size}")
                outputFileLines.add(newBooks.joinToString(" ") { book -> book.id.toString() })
            }
        }
    }

    // Write results
    File("outputs").mkdir()
    val outPutFile = File("outputs", file.name)
    outPutFile.delete()
    outputFileLines.add(0, nbLibApplied.toString())
    outputFileLines.forEach {
        outPutFile.appendText("$it \n")
    }

    println("${outPutFile.name}: totalDays: $totalDays, score: $sentScore/$totalBookScores, books: $sentBooks/$totalNumBooks, libs: $nbLibApplied/$totalLibs")
    return sentScore
}


fun scoreForNewBookUntil(library: Library, deadline: Int, alreadyScanBooks: Set<Int>): Int {
    val numOfNewBooks = numberOfNewBookUntil(library, deadline, alreadyScanBooks)
    return if (numOfNewBooks <= 0)
        0
    else
        library.books.filter { !alreadyScanBooks.contains(it.id) }.subList(0, numOfNewBooks).sumBy { it.score }

}

fun numberOfNewBookUntil(library: Library, deadline: Int, alreadyScanBooks: Set<Int>): Int {
    return if (deadline <= library.signupTime)
        0
    else
        min((deadline - library.signupTime) * library.shipPerDay,
                library.books.filter { !alreadyScanBooks.contains(it.id) }.size)
}

fun newBookUntil(library: Library, deadline: Int, alreadyScanBooks: Set<Int>): List<Book> {
    val newBooks = library.books.filter { !alreadyScanBooks.contains(it.id) }
    return if (deadline <= library.signupTime)
        emptyList()
    else if ((deadline - library.signupTime) * library.shipPerDay >= newBooks.size)
        newBooks else
        newBooks.subList(0, (deadline - library.signupTime) * library.shipPerDay)
}


data class Book(val id: Int, val score: Int)

data class Library(val id: Int, val signupTime: Int, val shipPerDay: Int, val books: MutableList<Book>)