package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.*

class RisConverterService(private val studyReviewIdGeneratorService: IdGeneratorService) {

    private val titleTypes = listOf("TI", "T1")
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

    fun convertManyToStudyReview(
        systematicStudyId: SystematicStudyId,
        searchSessionId: SearchSessionID,
        ris: String,
        source: MutableSet<String>,
    ): Pair<List<StudyReview>, List<String>> {
        require(ris.isNotBlank()) { "convertManyToStudyReview: RIS must not be blank." }

        val (validStudies, invalidEntries) = convertMany(ris)
        val studyReviews = validStudies.map { study -> convertToStudyReview(systematicStudyId, searchSessionId, study, source) }

        return Pair(studyReviews, invalidEntries)
    }

    private fun convertMany(ris: String): Pair<List<Study>, List<String>> {
        val validStudies = mutableListOf<Study>()
        val invalidEntries = mutableListOf<String>()

        val regex = Regex(
            "(?i)\\bTY\\b\\s*-.*?(?=(\\bTY\\b\\s*-|\\bER\\b\\s*-|\\z))",
            RegexOption.DOT_MATCHES_ALL
        )

        val entries = regex.findAll(ris).map { it.value.trim() }

        entries.forEach { entry ->
            try {
                val study = convert(entry)
                validStudies.add(study)
            } catch (e: Exception) {
                val entryName = extractInvalidRis(entry)
                val reason = e.message ?: "Invalid entry"
                invalidEntries.add("Entry '${summarizeRisEntry(entry)}': $reason")
            }
        }
        return Pair(validStudies, invalidEntries)
    }

    fun convert(ris: String): Study {
        require(ris.isNotBlank()) { "convert: RIS must not be blank." }

        val fieldMap = parseRisFields(ris)
        val venue = fieldMap["JO"]?.trim() ?: ""
        if (venue.isBlank()) throw IllegalArgumentException("Missing or invalid field 'venue' (JO)")

        val primaryTitle = getValueFromFieldMap(fieldMap, titleTypes).trim()
        val secondaryTitle = fieldMap["T2"]?.trim() ?: ""
        val fullTitle = ("$primaryTitle $secondaryTitle").trim()
        if (fullTitle.isBlank()) throw IllegalArgumentException("Missing or invalid field 'title' (TI/T1 or T2)")

        val year = fieldMap["PY"]?.trim()?.toIntOrNull()
            ?: fieldMap["Y1"]?.let { extractYear(it) }
            ?: throw IllegalArgumentException("Missing or invalid field 'year' (PY/Y1)")

        val authors = parseAuthors(fieldMap).trim()
        if (authors.isBlank()) throw IllegalArgumentException("Missing or invalid field 'authors' (AU/A1)")
        val type = extractStudyType(ris)
        val abs = fieldMap["AB"]?.trim() ?: ""
        if (abs.isBlank()) throw IllegalArgumentException("Missing or invalid field 'abstract' (AB)")
        val keywords = parseKeywords(fieldMap["KW"])
        val references = parseReferences(fieldMap["CR"])
        val doi = fieldMap["DO"]?.let {
            val clean = it.trim()
            if (clean.isBlank()) throw IllegalArgumentException("Invalid DOI: empty value")
            if (!clean.startsWith("10.")) throw IllegalArgumentException("Invalid DOI '$clean'")
            Doi("https://doi.org/$clean")
        }

        return Study(type, fullTitle, year, authors, venue, treatAbstract(abs), keywords, references, doi)
    }

    fun parseRisFields(ris: String): Map<String, String> {
        val fieldMap = mutableMapOf<String, String>()
        val lines = ris.trim().lines()
        var currentKey: String? = null

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.contains(" - ")) {
                val keyValuePair = trimmedLine.split(" - ", limit = 2)
                if (keyValuePair.size == 2) {
                    currentKey = keyValuePair[0].trim()
                    val value = keyValuePair[1].trim()
                    if (currentKey == "AU" || currentKey == "A1") {
                        fieldMap[currentKey] = fieldMap.getOrDefault(currentKey, "") + value + "; "
                    } else if (currentKey == "KW"){
                        fieldMap[currentKey] = fieldMap.getOrDefault(currentKey, "") + value + "; "
                    } else {
                        fieldMap[currentKey] = value
                    }
                }
            } else if (currentKey != null) {
                fieldMap[currentKey] = "${fieldMap[currentKey]} $trimmedLine".trim()
            }
        }
        return fieldMap
    }

    fun extractStudyType(risEntry: String): StudyType {
        val entryTypeRegex = Regex("""(?m)^TY\s*-\s*(.+)$""")
        val matchResult = entryTypeRegex.find(risEntry)
        val studyTypeName = translateToStudyType(matchResult?.groupValues?.get(1) ?: "")
        return studyTypeName
    }

    private fun translateToStudyType(studyType: String): StudyType {
        require(studyType.isNotBlank()) { "translateToStudyType: studytype must not be blank." }
        val risMap = mapOf(
            "ABST" to StudyType.ARTICLE,
            "ADVS" to StudyType.MISC,
            "AGGR" to StudyType.MISC,
            "ANCIENT" to StudyType.MISC,
            "ART" to StudyType.MISC,
            "BILL" to StudyType.MISC,
            "BLOG" to StudyType.MISC,
            "BOOK" to StudyType.BOOK,
            "CASE" to StudyType.MISC,
            "CHAP" to StudyType.INBOOK,
            "CHART" to StudyType.MISC,
            "CLSWK" to StudyType.MISC,
            "COMP" to StudyType.MISC,
            "CONF" to StudyType.PROCEEDINGS,
            "CPAPER" to StudyType.INPROCEEDINGS,
            "CTLG" to StudyType.MISC,
            "DATA" to StudyType.MISC,
            "DBASE" to StudyType.MISC,
            "DICT" to StudyType.MISC,
            "EBOOK" to StudyType.BOOK,
            "ECHAP" to StudyType.INBOOK,
            "EDBOOK" to StudyType.BOOK,
            "EJOUR" to StudyType.ARTICLE,
            "ELEC" to StudyType.MISC,
            "ENCYC" to StudyType.MISC,
            "EQUA" to StudyType.MISC,
            "FIGURE" to StudyType.MISC,
            "GEN" to StudyType.MISC,
            "GOVDOC" to StudyType.MISC,
            "GRNT" to StudyType.MISC,
            "HEAR" to StudyType.MISC,
            "ICOMM" to StudyType.MISC,
            "INPR" to StudyType.ARTICLE,
            "INTV" to StudyType.MISC,
            "JFULL" to StudyType.ARTICLE,
            "JOUR" to StudyType.ARTICLE,
            "LEGAL" to StudyType.MISC,
            "MANSCPT" to StudyType.UNPUBLISHED,
            "MAP" to StudyType.MISC,
            "MGZN" to StudyType.ARTICLE,
            "MPCT" to StudyType.MISC,
            "MULTI" to StudyType.MISC,
            "MUSIC" to StudyType.MISC,
            "NEWS" to StudyType.ARTICLE,
            "PAMP" to StudyType.BOOKLET,
            "PAT" to StudyType.MISC,
            "PCOMM" to StudyType.MISC,
            "POD" to StudyType.MISC,
            "PRESS" to StudyType.MISC,
            "RPRT" to StudyType.TECHREPORT,
            "SER" to StudyType.MISC,
            "SLIDE" to StudyType.MISC,
            "SOUND" to StudyType.MISC,
            "STAND" to StudyType.MISC,
            "STAT" to StudyType.MISC,
            "STD" to StudyType.MISC,
            "THES" to StudyType.MASTERSTHESIS,
            "UNBILL" to StudyType.MISC,
            "UNPB" to StudyType.UNPUBLISHED,
            "UNPD" to StudyType.UNPUBLISHED,
            "VIDEO" to StudyType.MISC
        )
        val st = risMap.getOrElse(studyType) { throw IllegalArgumentException() }
        return st
    }

    private fun getValueFromFieldMap(fieldMap: Map<String, String>, keys: List<String>): String {
        for (key in keys) {
            val value = fieldMap[key]
            if (value != null) return value
        }
        return ""
    }

    private fun extractYear(y1: String): Int? {
        val yearRegex = Regex("""\b\d{4}\b""")
        return yearRegex.find(y1)?.value?.toIntOrNull()
    }

    private fun parseKeywords(keywords: String?): Set<String> {
        return keywords?.split(";")?.map { it.trim() }?.filter { it.isNotBlank() }?.toSet()?: emptySet()
    }

    private fun parseReferences(references: String?): List<String> {
        return references?.split(";")?.map { it.trim() }?.filter { it.isNotBlank() }?.toList()?: emptyList()
    }

    private fun parseAuthors(fieldMap: Map<String, String>): String {
        val authorKeys = listOf("AU", "A1")
        val authors = authorKeys.flatMap { key ->
            fieldMap[key]?.split(";")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        }
        return authors.joinToString(", ")
    }

    private fun treatAbstract(abstract: String): String {
        val AB = abstract.split("ER").first().trim()
        return AB
    }

    private fun extractInvalidRis(risEntry: String): String {
        val risRegex = Regex("""TY\s*-\s*[\s\S]*?ER\s*-\s*""", RegexOption.MULTILINE)
        val matchResult = risRegex.find(risEntry)
        return matchResult?.value?.trim() ?: "UNKNOWN"
    }

    private fun summarizeRisEntry(risEntry: String): String {
        val map = parseRisFields(risEntry)
        val ti = map["TI"] ?: map["T1"] ?: map["T2"]
        if (!ti.isNullOrBlank()) return ti.trim().take(80)
        val tyRegex = Regex("""(?m)^TY\s*-\s*(.+)$""")
        val ty = tyRegex.find(risEntry)?.groupValues?.get(1)?.trim()
        return ty ?: "UNKNOWN"
    }
}