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

    private val ignoredEntryTypes = setOf("comment", "string", "preamble")

    private val entryStartRegex = Regex("""(?m)^[ \t]*@\s*(\w+)\s*\{""")

    private val entryTypeRegex = Regex("""@\s*(\w+)\s*\{""")
    private val entryKeyRegex = Regex("""@\s*\w+\s*\{([^,]*),""")
    private val yearRegex = Regex("""\d{4}""")

    private data class RawEntry(val text: String, val closed: Boolean)

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
        val studyReviewId = StudyReviewId(studyReviewIdGeneratorService.next(systematicStudyId.value()))
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

        splitEntries(bibtex.replace("\r", "")).forEach { raw ->
            val entryIdentifier = extractBibtexId(raw.text) ?: "starting with '${raw.text.take(40)}...'"

            if (!raw.closed) {
                invalidEntries.add("Failed to parse entry '$entryIdentifier': missing closing brace '}'.")
                return@forEach
            }

            try {
                validStudies.add(convert(raw.text))
            } catch (e: BibtexParseException) {
                invalidEntries.add("Failed to parse entry '$entryIdentifier': ${e.message}")
            } catch (e: Exception) {
                invalidEntries.add("An unexpected error occurred. Details: ${e.message}")
            }
        }
        return Pair(validStudies, invalidEntries)
    }

    fun convert(bibtexEntry: String): Study {
        require(bibtexEntry.isNotBlank()) { "BibTeX entry must not be blank." }

        val entry = bibtexEntry.replace("\r", "").trim()

        extractBibtexId(entry) ?: throw BibtexMissingRequiredFieldException("BibTeX ID")

        val type = extractStudyType(entry)
        val fieldMap = parseBibtexFields(entry)

        val title = fieldMap["title"]?.takeIf { it.isNotBlank() } ?: ""
        val year = fieldMap["year"]?.let { yearRegex.find(it)?.value?.toIntOrNull() } ?: 0
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

    private fun splitEntries(bibtex: String): List<RawEntry> {
        val entries = mutableListOf<RawEntry>()
        var pos = 0

        while (pos < bibtex.length) {
            val match = entryStartRegex.find(bibtex, pos) ?: break
            val start = match.range.first + match.value.indexOf('@')
            val openBrace = match.range.last

            var depth = 0
            var end = -1
            var i = openBrace
            while (i < bibtex.length) {
                when (bibtex[i]) {
                    '{' -> depth++
                    '}' -> {
                        depth--
                        if (depth == 0) {
                            end = i
                            break
                        }
                    }
                }
                i++
            }

            val type = match.groupValues[1].lowercase(Locale.ROOT)

            if (end == -1) {
                if (type !in ignoredEntryTypes) entries.add(RawEntry(bibtex.substring(start), closed = false))
                break
            }

            if (type !in ignoredEntryTypes) entries.add(RawEntry(bibtex.substring(start, end + 1), closed = true))
            pos = end + 1
        }
        return entries
    }

    private fun parseBibtexFields(bibtexEntry: String): Map<String, String> {
        val openBrace = bibtexEntry.indexOf('{')
        if (openBrace == -1) return emptyMap()

        val firstComma = bibtexEntry.indexOf(',', openBrace)
        if (firstComma == -1) return emptyMap()

        val closeBrace = bibtexEntry.lastIndexOf('}')
        val body = bibtexEntry.substring(firstComma + 1, if (closeBrace > firstComma) closeBrace else bibtexEntry.length)

        val fieldMap = mutableMapOf<String, String>()
        var i = 0

        while (i < body.length) {
            val eq = body.indexOf('=', i)
            if (eq == -1) break

            val key = body.substring(i, eq).trim().trim(',').trim().lowercase(Locale.ROOT)

            var j = eq + 1
            while (j < body.length && body[j].isWhitespace()) j++

            val value: String
            when {
                j < body.length && body[j] == '{' -> {
                    var depth = 0
                    var k = j
                    while (k < body.length) {
                        if (body[k] == '{') depth++
                        else if (body[k] == '}') {
                            depth--
                            if (depth == 0) break
                        }
                        k++
                    }
                    value = body.substring(j + 1, minOf(k, body.length))
                    i = k + 1
                }
                j < body.length && body[j] == '"' -> {
                    val k = body.indexOf('"', j + 1).let { if (it == -1) body.length else it }
                    value = body.substring(j + 1, k)
                    i = k + 1
                }
                else -> {
                    val k = body.indexOf(',', j).let { if (it == -1) body.length else it }
                    value = body.substring(j, k)
                    i = k + 1
                }
            }

            if (key.isNotEmpty() && key.none { it.isWhitespace() }) {
                fieldMap[key] = decodeHtmlEntities(value.trim())
            }
        }
        return fieldMap
    }

    private fun decodeHtmlEntities(value: String): String =
        value
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&amp;", "&")

    private fun getValueFromFieldMap(fieldMap: Map<String, String>, keys: List<String>): String {
        return keys.firstNotNullOfOrNull { key -> fieldMap[key] } ?: ""
    }

    private fun parseKeywords(keywords: String?): Set<String> {
        if (keywords == null) return emptySet()
        val separator = if (keywords.contains(';')) ";" else ","
        return keywords.split(separator).map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    private fun parseReferences(references: String?): List<String> {
        return references?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    }

    private fun extractStudyType(bibtexEntry: String): StudyType {
        val studyTypeName = entryTypeRegex.find(bibtexEntry)?.groupValues?.get(1)?.uppercase(Locale.ROOT)
            ?: return StudyType.UNKNOWN

        return runCatching { StudyType.valueOf(studyTypeName) }.getOrDefault(StudyType.UNKNOWN)
    }

    private fun extractBibtexId(bibtexEntry: String): String? {
        return entryKeyRegex.find(bibtexEntry)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() }
    }
}