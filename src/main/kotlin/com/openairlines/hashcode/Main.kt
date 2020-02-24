package com.openairlines.hashcode

import java.io.File
import java.lang.Integer.min

fun main() {
    val files = File("inputs").listFiles()?.asList()
    files?.sortedBy { it.name }
    files?.forEach {
        println(it.name)
        processFile(it)
    }
}

fun processFile(file: File) {
    // Read input
    val lines = file.readLines()
    val totalNumBooks = lines[0].split(" ")[0].toInt()
    val totalDays = lines[0].split(" ")[2].toInt()
    val allBookScores = lines[1].split(" ").map { it.toInt() }
    val totalBookScores = allBookScores.sum()

    val libraries = emptyList<Library>().toMutableList()
    for ((idLib, i) in (2 until lines.size - 1 step 2).withIndex()) {
        val elements = lines[i].split(" ")
        val books = lines[i + 1].split(" ").map {
            Book(it.toInt(), allBookScores[it.toInt()])
        }
        libraries.add(Library(idLib, elements[1].toInt(), elements[2].toInt(), books.sortedByDescending { it.score }.toMutableList()))
    }
    val totalLibs = libraries.size

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
                    it.numberOfNewBookUntil(remainingDays, scannedBooks) <= 0
        }
        libraries.sortByDescending { it.scoreForNewBookUntil(remainingDays, scannedBooks) / it.signupTime }
        libraries.firstOrNull()?.let { library ->
            libraries.remove(library)
            val newBooks = library.newBookUntil(remainingDays, scannedBooks)
            if (newBooks.isNotEmpty()) {
                // Just some indicators...
                sentScore += newBooks.sumBy { it.score }
                sentBooks += newBooks.size
                // Keep track of already scanned books
                scannedBooks.addAll(newBooks.map { it.id })
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

}


data class Book(val id: Int, val score: Int)

data class Library(val id: Int, val signupTime: Int, val shipPerDay: Int, val books: MutableList<Book>) {

    fun scoreForNewBookUntil(deadline: Int, alreadyScanBooks: Set<Int>): Int {
        val numOfNewBooks = numberOfNewBookUntil(deadline, alreadyScanBooks)
        return if (numOfNewBooks <= 0)
            0
        else
            books.filter { !alreadyScanBooks.contains(it.id) }.subList(0, numOfNewBooks).sumBy { it.score }

    }

    fun numberOfNewBookUntil(deadline: Int, alreadyScanBooks: Set<Int>): Int {
        return if (deadline <= signupTime)
            0
        else
            min((deadline - signupTime) * shipPerDay, books.filter { !alreadyScanBooks.contains(it.id) }.size)
    }

    fun newBookUntil(deadline: Int, alreadyScanBooks: Set<Int>): List<Book> {
        val newBooks = books.filter { !alreadyScanBooks.contains(it.id) }
        return if (deadline <= signupTime)
            emptyList()
        else if ((deadline - signupTime) * shipPerDay >= newBooks.size)
            newBooks else
            newBooks.subList(0, (deadline - signupTime) * shipPerDay)
    }

}