package br.all.utils

import java.io.File
import java.io.IOException

class BibSplitter {

    fun splitBySource(bibText: String): Map<String, MutableList<String>> {
        val bibBySource = mutableMapOf<String, MutableList<String>>()
        val entries = bibText.split(Regex("(?m)^@")).filter { it.isNotBlank() }
        for (entry in entries) {
            val bibEntry = "@" + entry.trim()
            val regex = Regex("source\\s*=\\s*\\{([^}]*)", RegexOption.IGNORE_CASE)
            val matchResult = regex.find(bibEntry)
            if (matchResult != null) {
                val sourceField = matchResult.groupValues[1]
                val sources = sourceField.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                for (src in sources) {
                    bibBySource.computeIfAbsent(src) { mutableListOf() }.add(bibEntry)
                }
            }
        }
        return bibBySource
    }

    @Throws(IOException::class)
    fun saveBibFiles(bibBySource: Map<String, List<String>>, outputDir: String) {
        val dir = File(outputDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        for ((source, entries) in bibBySource) {
            val fileName = source.replace("\\s+".toRegex(), "_") + ".bib"
            val outFile = File(dir, fileName)
            outFile.printWriter().use { out ->
                entries.forEach { entry ->
                    out.println(entry)
                    out.println()
                }
            }
        }
    }
}

fun main() {
    val inputFile = "web/src/main/kotlin/br/all/utils/ALL.bib"
    val outputDir = "web/src/main/kotlin/br/all/utils/separatedBibs"
    try {
        val bibText = File(inputFile).readText(Charsets.UTF_8)
        val splitter = BibSplitter()
        val bibBySource = splitter.splitBySource(bibText)
        println("Sources encontrados:")
        bibBySource.forEach { (src, entries) ->
            println(" - $src (${entries.size} entradas)")
        }
        splitter.saveBibFiles(bibBySource, outputDir)
        println("Arquivos gerados em: $outputDir")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
