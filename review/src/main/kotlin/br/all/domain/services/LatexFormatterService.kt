package br.all.domain.services

class LatexFormatterService {

    fun formatProtocol(protocol: ProtocolFto): String {
        return """
        \documentclass{article}
        \usepackage[utf8]{inputenc}
        \usepackage{enumitem}

        \title{Protocolo do Estudo: \textbf{${latexEscape(protocol.systematicStudy)}}}
        \author{}
        \date{\today}

        \begin{document}

        \maketitle

        \section*{Identificação}
        \begin{itemize}[leftmargin=*]
            \item \textbf{ID:} ${latexEscape(protocol.id)}
            \item \textbf{Estudo Sistemático:} ${latexEscape(protocol.systematicStudy)}
        \end{itemize}

        \section*{Objetivo}
        ${latexEscape(protocol.goal)}

        \section*{Justificativa}
        ${latexEscape(protocol.justification)}

        \section*{Perguntas de Pesquisa}
        \begin{itemize}[leftmargin=*]
        ${protocol.researchQuestions.joinToString("\n") { "\\item ${latexEscape(it)}" }}
        \end{itemize}

        \section*{Palavras-chave}
        ${protocol.keywords.joinToString(", ") { latexEscape(it) }}

        \section*{String de Busca}
        ${latexEscape(protocol.searchString)}

        \section*{Fontes de Informação}
        \begin{itemize}[leftmargin=*]
        ${protocol.informationSources.joinToString("\n") { "\\item ${latexEscape(it)}" }}
        \end{itemize}

        \section*{Critérios de Seleção}
        ${latexEscape(protocol.sourcesSelectionCriteria)}

        \section*{Método de Busca}
        ${latexEscape(protocol.searchMethod)}

        \section*{Idiomas dos Estudos}
        ${protocol.studiesLanguages.joinToString(", ") { latexEscape(it) }}

        \section*{Definição do Tipo de Estudo}
        ${latexEscape(protocol.studyTypeDefinition)}

        \section*{Processo de Seleção}
        ${latexEscape(protocol.selectionProcess)}

        \section*{Critérios de Elegibilidade}
        \begin{itemize}[leftmargin=*]
        ${protocol.eligibilityCriteria.joinToString("\n") { "\\item ${latexEscape(it)}" }}
        \end{itemize}

        \section*{Processo de Coleta de Dados}
        ${latexEscape(protocol.dataCollectionProcess)}

        \section*{Processo de Análise e Síntese}
        ${latexEscape(protocol.analysisAndSynthesisProcess)}

        \section*{Questões de Extração}
        \begin{itemize}[leftmargin=*]
        ${protocol.extractionQuestions.joinToString("\n") { "\\item ${latexEscape(it)}" }}
        \end{itemize}

        \section*{Questões de Avaliação da Qualidade (ROB)}
        \begin{itemize}[leftmargin=*]
        ${protocol.robQuestions.joinToString("\n") { "\\item ${latexEscape(it)}" }}
        \end{itemize}

        \section*{PICOC}
        ${latexEscape(protocol.picoc)}

        \end{document}
    """.trimIndent()
    }

    private fun latexEscape(text: String?): String {
        if (text.isNullOrBlank()) return ""
        return text
            .replace("\\", "\\textbackslash{}")
            .replace("_", "\\_")
            .replace("%", "\\%")
            .replace("&", "\\&")
            .replace("#", "\\#")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("$", "\\$")
    }
}