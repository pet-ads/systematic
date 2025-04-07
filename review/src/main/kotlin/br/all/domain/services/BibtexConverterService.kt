package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.*
import java.util.*

class BibtexConverterService(private val studyReviewIdGeneratorService: IdGeneratorService) {

    private val authorTypes = listOf("author", "authors", "editor")
    private val venueTypes = listOf("journal", "booktitle", "institution",
        "organization", "publisher", "series", "school", "howpublished")

    fun convertManyToStudyReview(
        systematicStudyId: SystematicStudyId,
        searchSessionId: SearchSessionID,
        bibtex: String,
        source: MutableSet<String>
    ): Pair<List<StudyReview>, List<String>> {
        require(bibtex.isNotBlank()) { "BibTeX must not be blank." }

        val (validStudies, invalidEntries) = convertMany(bibtex)
        val studyReviews = validStudies.map { study -> convertToStudyReview(systematicStudyId, searchSessionId, study, source) }

        return Pair(studyReviews, invalidEntries)
    }

    fun convertToStudyReview(systematicStudyId: SystematicStudyId, searchSessionId: SearchSessionID, study: Study, source: MutableSet<String>): StudyReview {
        val studyReviewId = StudyReviewId(studyReviewIdGeneratorService.next())

        return StudyReview(
            studyReviewId,
            systematicStudyId,
            searchSessionId,
            study.type,
            study.title,
            study.year,
            study.authors,
            study.venue,
            study.abstract,
            study.doi,
            study.keywords,
            source,
            study.references,
            mutableSetOf(),
            mutableSetOf(),
            mutableSetOf(),
            "",
            ReadingPriority.LOW,
            SelectionStatus.UNCLASSIFIED,
            ExtractionStatus.UNCLASSIFIED
        )
    }

    private fun convertMany(bibtex: String): Pair<List<Study>, List<String>> {
        val validStudies = mutableListOf<Study>()
        val invalidEntries = mutableListOf<String>()

        bibtex.splitToSequence("@")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { entry ->
                try {
                    val study = convert(entry)
                    validStudies.add(study)
                } catch (e: Exception) {
                    val entryName = extractEntryName(entry)
                    invalidEntries.add(entryName)
                }
            }
        return Pair(validStudies, invalidEntries)
    }

    fun convert(bibtexEntry: String): Study {
        require(bibtexEntry.isNotBlank()) { "BibTeX entry must not be blank." }

        val fieldMap = parseBibtexFields(bibtexEntry)

        val title = fieldMap["title"] ?: ""
        val year = fieldMap["year"]?.toIntOrNull() ?: 0
        val authors = getValueFromFieldMap(fieldMap, authorTypes)
        val venue = getValueFromFieldMap(fieldMap, venueTypes)
        val abstract = fieldMap["abstract"] ?: " "
        val keywords = parseKeywords(fieldMap["keywords"])
        val references = parseReferences(fieldMap["references"])
        val doi = fieldMap["doi"]?.let {
            val cleanDoi = it.replace(Regex("}"), "")
            Doi("https://doi.org/$cleanDoi")
        }

        val type = extractStudyType(bibtexEntry)

        return Study(type, title, year, authors, venue, abstract, keywords, references, doi)
    }

    private fun parseBibtexFields(bibtexEntry: String): Map<String, String> {
        val entry = bibtexEntry.replace("\n\t", " ")
        val fields = entry.trim().split("\n").map { it.trim() }
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
    }

     private fun extractEntryName(bibtexEntry: String): String {
        val nameRegex = Regex("""\{(.*)}""", RegexOption.DOT_MATCHES_ALL)
        val matchResult = nameRegex.find(bibtexEntry)
        return matchResult?.groupValues?.get(1)?.trim() ?: "UNKNOWN"
    }
}