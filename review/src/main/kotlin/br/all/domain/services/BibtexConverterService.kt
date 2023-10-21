package br.all.domain.services

import br.all.domain.model.study.*
import java.util.*

class BibtexConverterService {

    private val venueTypes = listOf(
        "journal", "booktitle", "institution", "organization",
        "publisher", "series", "school", "howpublished"
    )
    private val authorTypes = listOf("author", "authors", "editor")

//    fun convertToStudyReview(study: Study): StudyReview{
//        return StudyReview(
//            StudyReviewId(),
//            ReviewId(),
//            study.type,
//            study.title,
//            study.year,
//            study.authors,
//            study.venue,
//            study.abstract,
//            study.keywords,
//            mutableSetOf("insert SearchSources"),
//            study.references,
//            study.doi,
//            mutableSetOf(),
//            mutableMapOf(),
//            mutableMapOf(),
//            "",
//            ReadingPriority.LOW,
//            SelectionStatus.UNCLASSIFIED,
//            ExtractionStatus.UNCLASSIFIED
//        )
//    }

    fun convertMany(bibtex: String): List<Study> {
        require(bibtex.isNotBlank()) { "BibTeX must not be blank." }
        return bibtex.splitToSequence("@")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { convert(it) }
            .toList()
    }

    private fun convert(bibtexEntry: String): Study {
        require(bibtexEntry.isNotBlank()) { "BibTeX entry must not be blank." }

        val fieldMap = parseBibtexFields(bibtexEntry)

        val title = fieldMap["title"] ?: ""
        val year = fieldMap["year"]?.toIntOrNull() ?: 0
        val authors = getValueFromFieldMap(fieldMap, authorTypes)
        val venue = getValueFromFieldMap(fieldMap, venueTypes)
        val abstract = fieldMap["abstract"] ?: ""
        val keywords = parseKeywords(fieldMap["keywords"])
        val references = parseReferences(fieldMap["references"])
        val doi = fieldMap["doi"]?.let { Doi("https://doi.org/$it") }

        val type = extractStudyType(bibtexEntry)

        return Study(type, title, year, authors, venue, abstract, keywords, references, doi)
    }

    private fun parseBibtexFields(bibtexEntry: String): Map<String, String> {
        val fields = bibtexEntry.trim().split("\n").map { it.trim() }
        val fieldMap = mutableMapOf<String, String>()

        for (field in fields) {
            val keyValuePair = field.split("=")
            if (keyValuePair.size == 2) {
                val key = keyValuePair[0].trim()
                val value = cleanStringSurroundings(keyValuePair[1])
                fieldMap[key] = value
            }
        }
        return fieldMap
    }

    private fun cleanStringSurroundings(value: String): String {
        return value.trim().removePrefix("{").removeSuffix("}")
            .removeSuffix("},")
    }

    private fun getValueFromFieldMap(fieldMap: Map<String, String>, keys: List<String>): String {
        for (key in keys) {
            val value = fieldMap[key]
            if (value != null) return value
        }
        return ""
    }

    private fun parseKeywords(keywords: String?): Set<String> {
        return keywords?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()
    }

    private fun parseReferences(references: String?): List<String> {
        return references?.split(",")?.map { it.trim() } ?: emptyList()
    }

    private fun extractStudyType(bibtexEntry: String): StudyType {
        val entryTypeRegex = Regex("""\b(\w+)\{""")
        val matchResult = entryTypeRegex.find(bibtexEntry)
        val studyTypeName = matchResult?.groupValues?.get(1)?.uppercase(Locale.getDefault()) ?: "UNKNOWN"
        return StudyType.valueOf(studyTypeName)

        /*return when (studyTypeName){
            "article" -> StudyType.ARTICLE
            "book" -> StudyType.BOOK
            "booklet" -> StudyType.BOOKLET
            "inbook" -> StudyType.INBOOK
            "incollection" -> StudyType.INCOLLECTION
            "inproceedings" -> StudyType.INPROCEEDINGS
            "manual" -> StudyType.MANUAL
            "mastersthesis" -> StudyType.MASTERSTHESIS
            "misc" -> StudyType.MISC
            "phdthesis" -> StudyType.PHDTHESIS
            "proceedings" -> StudyType.PROCEEDINGS
            "techreport" -> StudyType.TECHREPORT
            "unpublished" -> StudyType.UNPUBLISHED
            else -> StudyType.UNKNOWN
        }*/
    }
}