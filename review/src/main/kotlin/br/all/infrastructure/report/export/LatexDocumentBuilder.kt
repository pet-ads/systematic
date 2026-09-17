package br.all.infrastructure.report.export

import br.all.application.report.export.BubbleChartExportData
import br.all.application.report.export.ExportItemExportData
import br.all.application.report.export.GroupedTableRow
import br.all.application.report.export.ItemTableExportData
import br.all.application.report.export.PicocExportData
import br.all.application.report.export.PieBarChartExportData
import br.all.application.report.export.ProtocolExportData
import br.all.application.report.export.StudyExportData
import br.all.application.report.export.SystematicReviewExportData
import br.all.application.report.export.TextualTableExportData
import br.all.application.report.export.service.VisualizationType
import java.util.Locale

class LatexDocumentBuilder : DocumentBuilder<String> {

    private val content = StringBuilder()
    private val END_ITEMIZE = """\end{itemize}"""
    private val BEGIN_ITEMIZE = """\begin{itemize}[leftmargin=*]"""

    private val pieColors = listOf(
        "blue!70", "red!60", "green!60!black", "orange!80",
        "violet!70", "cyan!70!black", "yellow!80!orange", "gray!60"
    )

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
        picoc(data.picoc) // logo após as RQs
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
        return this
    }

    override fun addFunnelImage(fileName: String): DocumentBuilder<String> {
        sectionHeader("Funil de Estudos")
        includeImage(fileName, width = "0.8")
        return this
    }

    override fun addConsolidatedStudiesTable(studies: List<StudyExportData>): DocumentBuilder<String> {
        if (studies.isEmpty()) return this
        sectionHeader("Estudos Considerados na Extração (Consolidado)")
        content.appendLine("""{\small""")
        content.appendLine("""\begin{longtable}{|c|p{3.5cm}|p{2.5cm}|p{2.5cm}|p{1.8cm}|p{2.5cm}|}""")
        content.appendLine("""\hline""")
        content.appendLine("""\textbf{ID} & \textbf{Título} & \textbf{Autor} & \textbf{Periódico} & \textbf{Tipo} & \textbf{Bases} \\ \hline""")
        content.appendLine("""\endhead""")
        studies.forEach { study ->
            content.appendLine(
                """${study.id} & ${escape(study.title)} & ${escape(study.authors)} & """ +
                        """${escape(study.venue)} & \seqsplit{${escape(study.type)}} & ${escape(study.bases.joinToString(", "))} \\ \hline"""
            )
        }
        content.appendLine("""\end{longtable}""")
        content.appendLine("""}""")
        return this
    }

    override fun addCriterionTable(title: String, rows: List<GroupedTableRow>): DocumentBuilder<String> {
        if (rows.isEmpty()) return this
        sectionHeader(title)
        appendCriterionTableBody(rows)
        return this
    }

    override fun addExportItem(item: ExportItemExportData): DocumentBuilder<String> {
        sectionHeader(item.questionDescription)
        when {
            item.textualTable != null -> appendTextualTableBody(item.textualTable)
            item.itemTable != null -> appendItemTableBody(item.itemTable)
            item.tableRows != null -> appendCriterionTableBody(item.tableRows)
            item.pieBarData != null && item.visualization == VisualizationType.PIE_CHART ->
                appendPieChart(item.pieBarData)
            item.pieBarData != null ->
                appendBarChart(item.pieBarData)
            item.bubbleData != null ->
                appendBubbleChart(item.bubbleData)
        }
        return this
    }

    private fun appendPieChart(data: PieBarChartExportData) {
        if (data.labels.isEmpty()) return
        val total = data.values.sum()
        if (total <= 0) return

        content.appendLine("""\begin{center}""")
        content.appendLine("""\begin{tikzpicture}""")
        var startAngle = 90.0
        val radius = 2.8
        data.labels.forEachIndexed { i, _ ->
            val value = data.values[i]
            val sweep = 360.0 * value / total
            val endAngle = startAngle - sweep
            val color = pieColors[i % pieColors.size]
            content.appendLine(
                """\draw[fill=$color, draw=white, line width=1pt] (0,0) -- """ +
                        """(${String.format(Locale.US, "%.3f", startAngle)}:$radius) arc """ +
                        """(${String.format(Locale.US, "%.3f", startAngle)}:${String.format(Locale.US, "%.3f", endAngle)}:$radius) -- cycle;"""
            )
            startAngle = endAngle
        }
        content.appendLine("""\end{tikzpicture}""")
        content.appendLine()
        content.appendLine(BEGIN_ITEMIZE)
        data.labels.forEachIndexed { i, label ->
            val color = pieColors[i % pieColors.size]
            val pct = String.format(Locale("pt", "BR"), "%.1f", data.values[i] * 100.0 / total)
            content.appendLine(
                """    \item[\textcolor{$color}{\rule{0.35cm}{0.35cm}}] """ +
                        """${escape(label)}: ${data.values[i]} ($pct\%)"""
            )
        }
        content.appendLine(END_ITEMIZE)
        content.appendLine("""\end{center}""")
    }

    private fun appendBarChart(data: PieBarChartExportData) {
        if (data.labels.isEmpty()) return
        val symbolicCoords = data.labels.joinToString(",") { sanitizePgfLabel(it) }
        val coords = data.labels.zip(data.values).joinToString(" ") { (label, value) ->
            "(${sanitizePgfLabel(label)},$value)"
        }
        content.appendLine("""\begin{center}""")
        content.appendLine("""\begin{tikzpicture}""")
        content.appendLine("""\begin{axis}[
            ybar,
            bar width=15pt,
            symbolic x coords={$symbolicCoords},
            xtick=data,
            x tick label style={rotate=45, anchor=east, font=\small},
            nodes near coords,
            ymin=0,
            width=0.95\linewidth,
            height=7cm,
            enlarge x limits=0.15
        ]""")
        content.appendLine("""\addplot coordinates {$coords};""")
        content.appendLine("""\end{axis}""")
        content.appendLine("""\end{tikzpicture}""")
        content.appendLine("""\end{center}""")
    }

    private fun appendBubbleChart(data: BubbleChartExportData) {
        if (data.points.isEmpty()) return
        val groups = data.points.map { it.group }.distinct()
        val groupIndex = groups.withIndex().associate { (i, g) -> g to i }
        val yTickLabels = groups.joinToString(",") { sanitizePgfLabel(it) }
        val yTicks = groups.indices.joinToString(",")

        content.appendLine("""\begin{center}""")
        content.appendLine("""\begin{tikzpicture}""")
        content.appendLine("""\begin{axis}[
        ytick={$yTicks},
        yticklabels={$yTickLabels},
        yticklabel style={font=\small},
        xlabel={Ano},
        xticklabel style={/pgf/number format/1000 sep=},
        width=0.95\linewidth,
        height=7cm
    ]""")
        content.appendLine("""\addplot[scatter, only marks, mark=*, blue, opacity=0.7,
        visualization depends on={\thisrow{count}\as\perpointmarksize},
        scatter/@pre marker code/.append style={/tikz/mark size=\perpointmarksize*1.5}
    ] table[meta=count] {""")
        content.appendLine("""x y count""")
        data.points.forEach { p ->
            content.appendLine("""${p.year} ${groupIndex[p.group]} ${p.count}""")
        }
        content.appendLine("""};""")
        content.appendLine("""\end{axis}""")
        content.appendLine("""\end{tikzpicture}""")
        content.appendLine("""\end{center}""")
    }

    private fun sanitizePgfLabel(label: String): String {
        return escape(label)
            .replace(",", ";")
            .replace("/", "-")
    }

    override fun build(): String = buildString {
        appendLine("""\documentclass{article}""")
        appendLine("""\usepackage[utf8]{inputenc}""")
        appendLine("""\usepackage[margin=1.5cm]{geometry}""")
        appendLine("""\usepackage{enumitem}""")
        appendLine("""\usepackage{graphicx}""")
        appendLine("""\usepackage{longtable}""")
        appendLine("""\usepackage{tikz}""")
        appendLine("""\usepackage{pgfplots}""")
        appendLine("""\pgfplotsset{compat=1.18}""")
        appendLine("""\usepackage{seqsplit}""")
        appendLine("""\begin{document}""")
        append(content)
        appendLine("""\end{document}""")
    }

    private fun appendCriterionTableBody(rows: List<GroupedTableRow>) {
        content.appendLine("""{\small""")
        content.appendLine("""\begin{longtable}{|p{3.5cm}|p{5.5cm}|c|c|}""")
        content.appendLine("""\hline""")
        content.appendLine("""\textbf{Critério} & \textbf{IDs dos Estudos} & \textbf{Qtd} & \textbf{\%} \\ \hline""")
        content.appendLine("""\endhead""")
        rows.forEach { row ->
            val ids = row.studyIds.joinToString(", ")
            content.appendLine(
                """${escape(row.criterion)} & ${escape(ids)} & ${row.count} & """ +
                        """${"%.1f".format(row.percentage)} \\ \hline"""
            )
        }
        content.appendLine("""\end{longtable}""")
        content.appendLine("""}""")
    }

    private fun appendItemTableBody(table: ItemTableExportData) {
        content.appendLine("""{\small""")
        val cols = "c|".repeat(table.options.size)
        content.appendLine("""\begin{longtable}{|c|$cols}""")
        content.appendLine("""\hline""")
        val header = listOf("ID") + table.options
        content.appendLine(header.joinToString(" & ") { """\textbf{${escape(it)}}""" } + """ \\ \hline""")
        content.appendLine("""\endhead""")
        table.rows.forEach { row ->
            val cells = listOf(row.studyId.toString()) +
                    table.options.map { if (row.selections[it] == true) "Sim" else "Não" }
            content.appendLine(cells.joinToString(" & ") + """ \\ \hline""")
        }
        content.appendLine("""\end{longtable}""")
        content.appendLine("""}""")
    }

    private fun appendTextualTableBody(table: TextualTableExportData) {
        content.appendLine("""{\small""")
        content.appendLine("""\begin{longtable}{|c|p{3cm}|p{2.5cm}|p{5.5cm}|}""")
        content.appendLine("""\hline""")
        content.appendLine("""\textbf{ID} & \textbf{Título} & \textbf{Autor} & \textbf{Resposta} \\ \hline""")
        content.appendLine("""\endhead""")
        table.rows.forEach { row ->
            content.appendLine(
                """${row.studyId} & ${escape(row.title)} & ${escape(row.authors)} & ${escape(row.answer)} \\ \hline"""
            )
        }
        content.appendLine("""\end{longtable}""")
        content.appendLine("""}""")
    }

    private fun includeImage(fileName: String, width: String) {
        content.appendLine("""\begin{center}""")
        content.appendLine("""\includegraphics[width=$width\textwidth]{$fileName}""")
        content.appendLine("""\end{center}""")
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