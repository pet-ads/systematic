package br.all.domain.services

import br.all.domain.model.study.Doi
import br.all.domain.model.study.Study


class BibtexConverterService {

    fun convertMany(bibtex: String): List<Study> {
        require(bibtex.isNotBlank()) { "BibTeX must not be blank." }
        val split = bibtex.split("@")
        return split.map { convert(it) }.toList()
    }

    //TODO Good solution. However, try to improve clean code (maybe creating auxiliary private methods)
    //TODO Try to make it flexible to obtain data from multiple types of bibtex entries (but keep it simple)
    private fun convert(bibtexEntry: String): Study {
        require(bibtexEntry.isNotBlank()) { "BibTeX entry must not be blank." }

        val fields = bibtexEntry.trim().split("\n").map { it.trim() }
        val fieldMap = mutableMapOf<String, String>()

        for (field in fields) {
            val keyValuePair = field.split("=")
            if (keyValuePair.size == 2) {
                val key = keyValuePair[0].trim()
                val value = keyValuePair[1].trim().removePrefix("{").removeSuffix("},").removeSuffix("}")
                fieldMap[key] = value
            }
        }

        val title = fieldMap["title"] ?: ""
        val year = fieldMap["year"]?.toIntOrNull() ?: 0
        val authors = fieldMap["author"] ?: ""
        val venue = fieldMap["journal"] ?: ""
        val abstract = fieldMap["abstract"] ?: ""
        val keywords = fieldMap["keywords"]?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()
        val references = fieldMap["references"]?.split(",")?.map { it.trim() } ?: emptyList()
        val doi = fieldMap["doi"]?.let { Doi(it) }

        return Study(title, year, authors, venue, abstract, keywords, references, doi)
    }
}