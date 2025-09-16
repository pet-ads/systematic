package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.*
import br.all.domain.shared.exception.ris.RisParseException
import br.all.domain.shared.exception.ris.RisMissingRequiredFieldException
import br.all.domain.shared.exception.ris.RisInvalidFieldFormatException
import br.all.domain.shared.exception.ris.RisUnknownEntryTypeException

class RisConverterService(private val studyReviewIdGeneratorService: IdGeneratorService) {

    private val titleTypes = listOf("TI", "T1")
    private val authorTypes = listOf("AU", "A1")
    private val yearTypes = listOf("PY", "Y1")
    private val venueTypes = listOf("JO", "T2", "JF", "JA")

    fun convertManyToStudyReview(
        systematicStudyId: SystematicStudyId,
        searchSessionId: SearchSessionID,
        ris: String,
        source: MutableSet<String>,
    ): Pair<List<StudyReview>, List<String>> {
        require(ris.isNotBlank()) { "RIS input must not be blank." }

        val (validStudies, invalidEntries) = convertMany(ris)
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


    private fun convertMany(ris: String): Pair<List<Study>, List<String>> {
        val validStudies = mutableListOf<Study>()
        val invalidEntries = mutableListOf<String>()

        val entryRegex = Regex("""(^TY\s*-.+?)(?=^\s*TY\s*-|\Z)""", setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))

        entryRegex.findAll(ris).forEach { matchResult ->
            val entry = matchResult.value.trim()
            val entryIdentifier = extractTitleForError(entry)
            try {
                if (entry.isNotBlank()) {
                    val study = convert(entry)
                    validStudies.add(study)
                }
            } catch (e: RisParseException) {
                invalidEntries.add("Entry '$entryIdentifier': ${e.message}")
            } catch (e: Exception) {
                invalidEntries.add("Entry '$entryIdentifier': An unexpected error occurred. ${e.message}")
            }
        }
        return Pair(validStudies, invalidEntries)
    }

    fun convert(ris: String): Study {
        require(ris.isNotBlank()) { "RIS entry must not be blank." }

        val fieldMap = parseRisFields(ris)

        val type = extractStudyType(fieldMap)

        val primaryTitle = getValueFromFieldMap(fieldMap, titleTypes)
            .takeIf { it.isNotBlank() } ?: throw RisMissingRequiredFieldException("Title (TI or T1)")
        val secondaryTitle = fieldMap["T2"] ?: ""
        val title = "$primaryTitle $secondaryTitle".trim()

        val year = extractYear(fieldMap)
            ?: throw RisMissingRequiredFieldException("Year (PY or Y1)")

        val authors = parseAuthors(fieldMap)
            .takeIf { it.isNotBlank() } ?: throw RisMissingRequiredFieldException("Author (AU or A1)")

        val venue = getValueFromFieldMap(fieldMap, venueTypes)
        val abstract = fieldMap["AB"] ?: ""
        val keywords = parseKeywords(fieldMap["KW"])
        val references = parseReferences(fieldMap["CR"])

        val doi = fieldMap["DO"]?.let {
            try {
                Doi("https://doi.org/${it.trim()}")
            } catch (e: Exception) {
                throw RisInvalidFieldFormatException("DOI (DO)", it, "a valid DOI identifier")
            }
        }

        return Study(type, title, year, authors, venue, abstract, keywords, references, doi)
    }

    private fun parseRisFields(ris: String): Map<String, String> {
        val multiMap = mutableMapOf<String, MutableList<String>>()
        val lines = ris.trim().lines()
        val tagRegex = Regex("""^([A-Z0-9]{2})\s*-\s?(.*)""")
        var currentKey: String? = null

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isBlank()) continue

            val match = tagRegex.find(trimmedLine)
            if (match != null) {
                currentKey = match.groupValues[1]
                val value = match.groupValues[2].trim()
                multiMap.getOrPut(currentKey) { mutableListOf() }.add(value)
            } else if (currentKey != null) {
                val values = multiMap[currentKey]
                if (values != null && values.isNotEmpty()) {
                    val lastIndex = values.lastIndex
                    values[lastIndex] = values[lastIndex] + " " + trimmedLine
                }
            }
        }

        return multiMap.mapValues { (_, valueList) ->
            valueList.joinToString(";")
        }
    }

    private fun extractStudyType(fieldMap: Map<String, String>): StudyType {
        val typeCode = fieldMap["TY"] ?: throw RisMissingRequiredFieldException("Entry Type (TY)")
        return translateToStudyType(typeCode)
    }

    private fun translateToStudyType(risType: String): StudyType {
        return when (risType.trim().uppercase()) {
            "JOUR", "EJOUR", "JFULL", "ABST", "INPR", "MGZN", "NEWS" -> StudyType.ARTICLE
            "BOOK", "EBOOK", "EDBOOK" -> StudyType.BOOK
            "CHAP", "ECHAP" -> StudyType.INBOOK
            "CONF" -> StudyType.PROCEEDINGS
            "CPAPER" -> StudyType.INPROCEEDINGS
            "RPRT" -> StudyType.TECHREPORT
            "THES" -> StudyType.MASTERSTHESIS
            "PAMP" -> StudyType.BOOKLET
            "MANSCPT", "UNPB", "UNPD" -> StudyType.UNPUBLISHED
            "ADVS", "AGGR", "ANCIENT", "ART", "BILL", "BLOG", "CASE", "CHART", "CLSWK", "COMP", "CTLG", "DATA", "DBASE", "DICT", "ELEC", "ENCYC", "EQUA", "FIGURE", "GEN", "GOVDOC", "GRNT", "HEAR", "ICOMM", "INTV", "LEGAL", "MAP", "MPCT", "MULTI", "MUSIC", "PAT", "PCOMM", "POD", "PRESS", "SER", "SLIDE", "SOUND", "STAND", "STAT", "STD", "UNBILL", "VIDEO" -> StudyType.MISC
            else -> throw RisUnknownEntryTypeException(risType)
        }
    }

    private fun getValueFromFieldMap(fieldMap: Map<String, String>, keys: List<String>): String {
        return keys.firstNotNullOfOrNull { key -> fieldMap[key] } ?: ""
    }

    private fun extractYear(fieldMap: Map<String, String>): Int? {
        val yearString = getValueFromFieldMap(fieldMap, yearTypes)
        return yearString.let { Regex("""\b(\d{4})\b""").find(it)?.value?.toIntOrNull() }
    }

    private fun parseKeywords(keywords: String?): Set<String> {
        return keywords?.split(';')?.map { it.trim() }?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }

    private fun parseReferences(references: String?): List<String> {
        return references?.split(';')?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
    }

    private fun parseAuthors(fieldMap: Map<String, String>): String {
        val authors = getValueFromFieldMap(fieldMap, authorTypes)
        return authors.split(';')
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(separator = ", ")
    }

    private fun extractTitleForError(risEntry: String): String {
        val titleRegex = Regex("""^[T1|I]\s*-\s*(.+)$""", RegexOption.MULTILINE)
        return titleRegex.find(risEntry)?.groupValues?.get(1)?.trim() ?: "Unknown Title"
    }
}