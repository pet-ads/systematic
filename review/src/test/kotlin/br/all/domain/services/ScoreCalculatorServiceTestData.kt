package br.all.domain.services

import br.all.domain.model.protocol.Criterion
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.*
import java.util.*

object ScoreCalculatorServiceTestData {
    val baseStudyReview = StudyReview(
        studyId = StudyReviewId(1L),
        systematicStudyId = SystematicStudyId(UUID.randomUUID()),
        searchSessionId = SearchSessionID(UUID.randomUUID()),
        studyType = StudyType.ARTICLE,
        title = "Crescimento da dengue em países tropicais",
        year = 2025,
        authors = "Fulano de Tal",
        venue = "Universidade X",
        abstract = "O clima atual favorece a reprodução do mosquito Aedes aegypti, aumentando o crescimento da dengue em países tropicais.",
        doi = Doi("https://doi.org/10.1234/dengue.2025.001"),
        keywords = setOf("dengue", "mosquito", "países tropicais"),
        searchSources = mutableSetOf("PubMed", "SciELO"),
        references = emptyList(),
        criteria = mutableSetOf(Criterion("test", Criterion.CriterionType.INCLUSION)),
        formAnswers = mutableSetOf(Answer(UUID.randomUUID(), String)),
        robAnswers = mutableSetOf(Answer(UUID.randomUUID(), String)),
        comments = "Comentário de revisão 1",
        readingPriority = ReadingPriority.LOW,
        selectionStatus = SelectionStatus.INCLUDED,
        extractionStatus = ExtractionStatus.INCLUDED
    )

    val noMatchStudyReview = StudyReview(
        studyId = StudyReviewId(1L),
        systematicStudyId = SystematicStudyId(UUID.randomUUID()),
        searchSessionId = SearchSessionID(UUID.randomUUID()),
        studyType = StudyType.ARTICLE,
        title = "Tecnologias de computação quântica na medicina moderna",
        year = 2025,
        authors = "Fulano de Tal",
        venue = "Universidade X",
        abstract = "Estudo de algoritmos de machine learning aplicados à genômica usando computação quântica",
        doi = Doi("https://doi.org/10.1234/dengue.2025.001"),
        keywords = setOf("computação quântica", "machine learning", "genômica"),
        searchSources = mutableSetOf("a", "b"),
        references = emptyList(),
        criteria = mutableSetOf(Criterion("test", Criterion.CriterionType.INCLUSION)),
        formAnswers = mutableSetOf(Answer(UUID.randomUUID(), String)),
        robAnswers = mutableSetOf(Answer(UUID.randomUUID(), String)),
        comments = "Comentário de revisão 1",
        readingPriority = ReadingPriority.LOW,
        selectionStatus = SelectionStatus.INCLUDED,
        extractionStatus = ExtractionStatus.INCLUDED
    )

    val partialMatchStudyReview = StudyReview(
        studyId = StudyReviewId(1L),
        systematicStudyId = SystematicStudyId(UUID.randomUUID()),
        searchSessionId = SearchSessionID(UUID.randomUUID()),
        studyType = StudyType.ARTICLE,
        title = "Dengue em regiões urbanas",
        year = 2025,
        authors = "Fulano de Tal",
        venue = "Universidade X",
        abstract = "Análise de fatores sociais e ambientais que afetam a propagação da dengue.",
        doi = Doi("https://doi.org/10.1234/dengue.2025.001"),
        keywords = setOf("dengue", "regiões urbanas", "clima"),
        searchSources = mutableSetOf("a", "b"),
        references = emptyList(),
        criteria = mutableSetOf(Criterion("test", Criterion.CriterionType.INCLUSION)),
        formAnswers = mutableSetOf(Answer(UUID.randomUUID(), String)),
        robAnswers = mutableSetOf(Answer(UUID.randomUUID(), String)),
        comments = "Comentário de revisão 1",
        readingPriority = ReadingPriority.LOW,
        selectionStatus = SelectionStatus.INCLUDED,
        extractionStatus = ExtractionStatus.INCLUDED
    )

    val nullAbstractStudyReview = StudyReview(
        studyId = StudyReviewId(1L),
        systematicStudyId = SystematicStudyId(UUID.randomUUID()),
        searchSessionId = SearchSessionID(UUID.randomUUID()),
        studyType = StudyType.ARTICLE,
        title = "Crescimento da dengue em países tropicais",
        year = 2025,
        authors = "Fulano de Tal",
        venue = "Universidade X",
        abstract = null,
        doi = Doi("https://doi.org/10.1234/dengue.2025.001"),
        keywords = setOf("dengue", "mosquito", "países tropicais"),
        searchSources = mutableSetOf("PubMed", "SciELO"),
        references = emptyList(),
        criteria = mutableSetOf(Criterion("test", Criterion.CriterionType.INCLUSION)),
        formAnswers = mutableSetOf(Answer(UUID.randomUUID(), String)),
        robAnswers = mutableSetOf(Answer(UUID.randomUUID(), String)),
        comments = "Comentário de revisão 1",
        readingPriority = ReadingPriority.LOW,
        selectionStatus = SelectionStatus.INCLUDED,
        extractionStatus = ExtractionStatus.INCLUDED
    )
}
