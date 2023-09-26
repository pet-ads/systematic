package br.all.domain.services

import br.all.domain.model.study.Doi
import br.all.domain.model.study.Study


class BibtexToStudyReviewListConverterService {

    fun convertBibtexToStudyList(bibTeX: String): List<Study> {
        val studies = mutableListOf<Study>()
        val entries = bibTeX.split("@")

        for (entry in entries) {
            if (entry.isNotBlank()) {
                val fields = entry.trim().split("\n").map { it.trim() }
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

                val study = Study(title, year, authors, venue, abstract, keywords, references, doi)
                studies.add(study)
            }
        }
        return studies
    }
}