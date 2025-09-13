package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.Doi
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.ReadingPriority
import br.all.domain.model.study.SelectionStatus
import br.all.domain.model.study.Study
import br.all.domain.model.study.StudyReview
import br.all.domain.model.study.StudyReviewId
import br.all.domain.model.study.StudyType
import java.util.Locale

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
                    val entryKey = extractEntryKey(entry)
                    val reason = e.message ?: "Invalid entry"
                    invalidEntries.add("Entry '$entryKey': $reason")
                }
            }
        return Pair(validStudies, invalidEntries)
    }

    fun convert(bibtexEntry: String): Study {
        require(bibtexEntry.isNotBlank()) { "BibTeX entry must not be blank." }

        val fieldMap = parseBibtexFields(bibtexEntry)

        val title = fieldMap["title"]?.trim() ?: ""
        if (title.isBlank()) throw IllegalArgumentException("Missing or invalid field 'title'")

        val yearStr = fieldMap["year"]?.trim()
        val year = yearStr?.toIntOrNull()
            ?: throw IllegalArgumentException("Missing or invalid field 'year': '${yearStr ?: "null"}'")

        val authors = getValueFromFieldMap(fieldMap, authorTypes).trim()
        if (authors.isBlank()) throw IllegalArgumentException("Missing or invalid field 'author/authors/editor'")

        val venue = getValueFromFieldMap(fieldMap, venueTypes).trim()
        if (venue.isBlank()) throw IllegalArgumentException("Missing or invalid field 'venue' (journal/booktitle/institution/organization/publisher/series/school/howpublished)")

        val abstract = (fieldMap["abstract"] ?: "").trim()
        if (abstract.isBlank()) throw IllegalArgumentException("Missing or invalid field 'abstract'")

        val keywords = parseKeywords(fieldMap["keywords"] ?: fieldMap["keyword"])
        val references = parseReferences(fieldMap["references"])
        val doi = fieldMap["doi"]?.let {
            val cleanDoi = it.replace(Regex("[{}]"), "").trim()
            if (cleanDoi.isBlank()) throw IllegalArgumentException("Invalid DOI: empty value")
            val fullUrl = if (cleanDoi.startsWith("http")) {
                cleanDoi
            } else {
                if (!cleanDoi.startsWith("10.")) throw IllegalArgumentException("Invalid DOI '$cleanDoi'")
                "https://doi.org/$cleanDoi"
            }
            Doi(fullUrl)
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
        return keywords?.split(",", ";")?.map { it.trim() }?.toSet() ?: emptySet()
    }

    private fun parseReferences(references: String?): List<String> {
        return references?.split(",")?.map { it.trim() } ?: emptyList()
    }

    private fun extractStudyType(bibtexEntry: String): StudyType {
        val entryTypeRegex = Regex("""^\s*@?\s*([A-Za-z]+)\s*\{""")
        val matchResult = entryTypeRegex.find(bibtexEntry)
        val rawType = matchResult?.groupValues?.get(1) ?: ""
        val studyTypeName = rawType.uppercase(Locale.getDefault())
        return try {
            StudyType.valueOf(studyTypeName)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid or unsupported type '$rawType'")
        }
    }

    private fun extractEntryKey(bibtexEntry: String): String {
        val keyRegex = Regex("""^\s*@?\s*\w+\s*\{\s*([^,}]+)\s*,""", RegexOption.DOT_MATCHES_ALL)
        val matchResult = keyRegex.find(bibtexEntry)
        return matchResult?.groupValues?.get(1)?.trim() ?: "UNKNOWN"
    }
}