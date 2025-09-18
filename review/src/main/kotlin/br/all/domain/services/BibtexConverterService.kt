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
import br.all.domain.shared.exception.bibtex.BibtexMissingRequiredFieldException
import br.all.domain.shared.exception.bibtex.BibtexParseException
import java.util.Locale

class BibtexConverterService(private val studyReviewIdGeneratorService: IdGeneratorService) {

    private val authorTypes = listOf("author", "authors", "editor")
    private val venueTypes = listOf("journal", "booktitle", "institution", "organization", "publisher", "series", "school", "howpublished")

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

        bibtex.split(Regex("(?=\\s*@)"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { entry ->
                try {
                    val study = convert(entry)
                    validStudies.add(study)
                } catch (e: BibtexParseException) {
                    val entryIdentifier = extractBibtexId(entry) ?: "starting with '${entry.take(40)}...'"
                    invalidEntries.add("Failed to parse entry '$entryIdentifier': ${e.message}")
                } catch (e: Exception) {
                    val entryIdentifier = extractBibtexId(entry) ?: "starting with '${entry.take(40)}...'"
                    invalidEntries.add("An unexpected error occurred for entry '$entryIdentifier'. Details: ${e.message}")
                }
            }
        return Pair(validStudies, invalidEntries)
    }

    fun convert(bibtexEntry: String): Study {
        require(bibtexEntry.isNotBlank()) { "BibTeX entry must not be blank." }

        val bibtexId = extractBibtexId(bibtexEntry)
            ?: throw BibtexMissingRequiredFieldException("BibTeX ID")

        val type = extractStudyType(bibtexEntry)
        val fieldMap = parseBibtexFields(bibtexEntry)

        val title = fieldMap["title"]?.takeIf { it.isNotBlank() } ?: ""
        val year = fieldMap["year"]?.toIntOrNull() ?: 0
        val authors = getValueFromFieldMap(fieldMap, authorTypes).takeIf { it.isNotBlank() } ?: ""
        val venue = getValueFromFieldMap(fieldMap, venueTypes).takeIf { it.isNotBlank() } ?: "d"

        val abstract = fieldMap["abstract"] ?: ""
        val keywords = parseKeywords(fieldMap["keywords"] ?: fieldMap["keyword"])
        val references = parseReferences(fieldMap["references"])

        val doi = fieldMap["doi"]?.let {
            try {
                val cleanDoi = it.replace(Regex("[{}]"), "").trim()
                val fullUrl = if (cleanDoi.startsWith("http")) cleanDoi else "https://doi.org/$cleanDoi"
                Doi(fullUrl)
            } catch (e: Exception) {
                null
            }
        }

        return Study(type, title, year, authors, venue, abstract, keywords, references, doi)
    }

    private fun parseBibtexFields(bibtexEntry: String): Map<String, String> {
        val content = bibtexEntry.substringAfter('{', "").substringBeforeLast('}', "")
        if (content.isBlank()) {
            return emptyMap()
        }

        val fieldMap = mutableMapOf<String, String>()
        val fieldSplitRegex = Regex(""",\s*(?=\w+\s*=)""")

        content.split(fieldSplitRegex).forEach { fieldString ->
            val parts = fieldString.trim().split("=", limit = 2)
            if (parts.size == 2) {
                val key = parts[0].trim().lowercase(Locale.getDefault())
                val value = parts[1].trim()
                    .removeSurrounding("{", "}")
                    .removeSurrounding("\"", "\"")
                fieldMap[key] = value
            }
        }
        return fieldMap
    }

    private fun getValueFromFieldMap(fieldMap: Map<String, String>, keys: List<String>): String {
        return keys.firstNotNullOfOrNull { key -> fieldMap[key] } ?: ""
    }

    private fun parseKeywords(keywords: String?): Set<String> {
        return keywords?.split("[,;]".toRegex())?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
    }

    private fun parseReferences(references: String?): List<String> {
        return references?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    }

    private fun extractStudyType(bibtexEntry: String): StudyType {
        val entryTypeRegex = Regex("""@(\w+)\s*\{""")
        val matchResult = entryTypeRegex.find(bibtexEntry)
        val studyTypeName = matchResult?.groupValues?.get(1)?.uppercase(Locale.getDefault()) ?: return StudyType.UNKNOWN

        return runCatching { StudyType.valueOf(studyTypeName) }.getOrDefault(StudyType.UNKNOWN)
    }

    private fun extractBibtexId(bibtexEntry: String): String? {
        val keyRegex = Regex("""@\w+\s*\{(.*?)\s*,""", RegexOption.DOT_MATCHES_ALL)
        return keyRegex.find(bibtexEntry)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() }
    }
}