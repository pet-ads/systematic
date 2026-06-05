package br.all.infrastructure.report.export

import br.all.application.report.export.FunnelExportData
import br.all.application.report.export.PicocExportData
import br.all.application.report.export.ProtocolExportData
import br.all.application.report.export.StudyExportData
import br.all.application.report.export.SystematicReviewExportData

class LatexDocumentBuilder : DocumentBuilder<String> {

    private val content = StringBuilder()

    private val END_ITEMIZE = """\end{itemize}"""
    private val BEGIN_ITEMIZE = """\begin{itemize}[leftmargin=*]"""
    private val BEGIN_ITEMIZE_NESTED = """    \begin{itemize}"""
    private val END_ITEMIZE_NESTED = """    \end{itemize}"""


    override fun addSystematicStudy(data: SystematicReviewExportData): DocumentBuilder<String> {
        content.appendLine("""\begin{center}""")
        content.appendLine("""{\LARGE \textbf{${escape(data.title)}}}""")
        content.appendLine("""\end{center}""")
        content.appendLine("""\noindent\rule{\textwidth}{0.4pt}""")
        content.appendLine("""\vspace{0.5em}""")
        content.appendLine(escape(data.description))
        content.appendLine()
        content.appendLine("""\medskip\noindent\textbf{Objectives:} ${escape(data.objectives)}""")
        content.appendLine()
        content.appendLine("""\medskip\noindent\textbf{Owner:} ${data.owner}""")
        if (data.collaborators.isNotEmpty()) {
            content.appendLine()
            content.appendLine("""\medskip\noindent\textbf{Collaborators:} ${data.collaborators.joinToString(", ")}""")
        }
        content.appendLine("""\vspace{1em}""")
        return this
    }

    override fun addProtocol(data: ProtocolExportData): DocumentBuilder<String> {
        sectionHeader("Protocol")
        subsection("Goal", data.goal)
        subsection("Justification", data.justification)
        subsectionWithList("Research Questions", data.researchQuestions)
        subsectionWithList("Keywords", data.keywords)
        subsection("Search String", data.searchString)
        subsectionWithList("Information Sources", data.informationSources)
        subsection("Sources Selection Criteria", data.sourcesSelectionCriteria)
        subsection("Search Method", data.searchMethod)
        subsectionWithList("Studies Languages", data.studiesLanguages)
        subsection("Study Type Definition", data.studyTypeDefinition)
        subsection("Selection Process", data.selectionProcess)
        subsectionWithList("Inclusion Criteria", data.inclusionCriteria())
        subsectionWithList("Exclusion Criteria", data.exclusionCriteria())
        subsection("Data Collection Process", data.dataCollectionProcess)
        subsection("Analysis and Synthesis Process", data.analysisAndSynthesisProcess)
        subsectionWithList("Extraction Questions", data.extractionQuestions)
        subsectionWithList("Risk of Bias Questions", data.robQuestions)
        picoc(data.picoc)
        return this
    }

    override fun addFunnel(data: FunnelExportData): DocumentBuilder<String> {
        sectionHeader("Studies Funnel")
        content.appendLine(BEGIN_ITEMIZE)
        content.appendLine("""    \item Total identified: ${data.totalIdentifiedBySource.values.sum()}""")
        content.appendLine("""    \item After duplicates removed: ${data.totalAfterDuplicatesRemovedBySource.values.sum()}""")
        content.appendLine("""    \item Screened: ${data.totalScreened}""")

        content.appendLine("""    \item Excluded in screening: ${data.totalExcludedInScreening}""")
        if (data.excludedByCriterion.isNotEmpty()) {
            content.appendLine(BEGIN_ITEMIZE_NESTED)
            data.excludedByCriterion.forEach { (criterion, count) ->
                content.appendLine("""        \item ${escape(criterion)}: $count""")
            }
            content.appendLine(END_ITEMIZE_NESTED)
        }

        content.appendLine("""    \item Full text assessed: ${data.totalFullTextAssessed}""")

        content.appendLine("""    \item Excluded in full text: ${data.totalExcludedInFullText}""")
        if (data.totalExcludedByCriterion.isNotEmpty()) {
            content.appendLine(BEGIN_ITEMIZE_NESTED)
            data.totalExcludedByCriterion.forEach { (criterion, count) ->
                content.appendLine("""        \item ${escape(criterion)}: $count""")
            }
            content.appendLine(END_ITEMIZE_NESTED)
        }
        content.appendLine("""    \item Included: ${data.totalIncluded}""")

        content.appendLine(END_ITEMIZE)
        return this
    }

    override fun addStudies(
        includedInScreening: List<StudyExportData>,
        excludedInScreening: List<StudyExportData>,
        included: List<StudyExportData>,
        excludedInFullText: List<StudyExportData>,
    ): DocumentBuilder<String> {

        sectionWithStudyList("Studies Included in Screening", includedInScreening)
        sectionWithStudyList("Studies Excluded in Screening", excludedInScreening)

        sectionHeader("Included Studies")
        included.forEach { addStudyIncludedInExtraction(it) }

        sectionWithStudyList("Studies Excluded in Full Text", excludedInFullText)

        return this
    }

    override fun build(): String = buildString {
        appendLine("""\documentclass{article}""")
        appendLine("""\usepackage[utf8]{inputenc}""")
        appendLine("""\usepackage{enumitem}""")
        appendLine("""\begin{document}""")
        append(content)
        appendLine("""\end{document}""")
    }

    private fun picoc(picoc: PicocExportData?) {
        if (picoc == null) return
        val items = listOfNotNull(
            picoc.population?.let { "Population: $it" },
            picoc.intervention?.let { "Intervention: $it" },
            picoc.control?.let { "Control: $it" },
            picoc.outcome?.let { "Outcome: $it" },
            picoc.context?.let { "Context: $it" },
        )
        subsectionWithList("PICOC", items)
    }


    private fun addStudyIncludedInExtraction(data: StudyExportData): DocumentBuilder<String> {
        content.appendLine("""\subsection*{${escape(data.title)} (${data.year})}""")
        content.appendLine("""\noindent ${escape(data.authors)}. ${escape(data.venue)}""")

        val fields = listOf(
            "Selection Criteria" to data.selectionCriteria.toList(),
            "Extraction Answers" to data.extractionAnswers.map { "${it.question}: ${it.answer ?: "N/A"}" },
            "Risk of Bias Answers" to data.robAnswers.map { "${it.question}: ${it.answer ?: "N/A"}" },
        )
        fields.filter { it.second.isNotEmpty() }.forEach { (title, items) ->
            content.appendLine()
            content.appendLine("""\medskip\noindent\textbf{${escape(title)}}""")
            content.appendLine(BEGIN_ITEMIZE)
            items.forEach { content.appendLine("""    \item ${escape(it)}""") }
            content.appendLine(END_ITEMIZE)
        }

        return this
    }





    private fun sectionHeader(title: String) {
        content.appendLine("""\section{${escape(title)}}""")
    }

    private fun subsection(title: String, text: String) {
        if (text.isBlank()) return
        content.appendLine("""\subsection{${escape(title)}}""")
        content.appendLine(escape(text))
    }

    private fun subsectionWithList(title: String, items: List<String>) {
        if (items.isEmpty()) return
        content.appendLine("""\subsection{${escape(title)}}""")
        content.appendLine(BEGIN_ITEMIZE)
        items.forEach { content.appendLine("""    \item ${escape(it)}""") }
        content.appendLine(END_ITEMIZE)
    }

    private fun sectionWithStudyList(title: String, studies: List<StudyExportData>) {
        if (studies.isEmpty()) return
        content.appendLine("""\section{${escape(title)}}""")
        content.appendLine(BEGIN_ITEMIZE)
        studies.forEach { study ->
            content.appendLine("""    \item ${escape(study.title)} (${study.year})""")
            if (study.selectionCriteria.isNotEmpty()) {
                content.appendLine(BEGIN_ITEMIZE_NESTED)
                content.appendLine("""        \item ${study.selectionCriteria.joinToString(", ")}""")
                content.appendLine(END_ITEMIZE_NESTED)
            }
        }
        content.appendLine(END_ITEMIZE)
    }

    private fun escape(text: String): String {
        return text.replace("\\", """\textbackslash{}""")
            .replace("_", """\_""")
            .replace("%", """\%""")
            .replace("&", """\&""")
            .replace("#", """\#""")
            .replace("{", """\{""")
            .replace("}", """\}""")
            .replace("$", """\$""")
    }
}
