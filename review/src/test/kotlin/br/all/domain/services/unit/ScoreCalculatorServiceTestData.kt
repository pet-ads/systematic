package br.all.domain.services.unit

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.model.study.StudyReview
import br.all.domain.model.study.StudyReviewId
import br.all.domain.model.study.StudyType
import java.util.UUID

object ScoreCalculatorServiceTestData {
    private val systematicStudyId = SystematicStudyId(UUID.randomUUID())
    private val searchSessionId = SearchSessionID(UUID.randomUUID())
    private val searchSources = mutableSetOf("TestSource")

    fun createStudyReview(
        title: String,
        abstract: String? = null,
        keywords: Set<String> = emptySet()
    ): StudyReview {
        return StudyReview(
            studyId = StudyReviewId(1L),
            systematicStudyId = systematicStudyId,
            searchSessionId = searchSessionId,
            studyType = StudyType.ARTICLE,
            title = title,
            year = 2025,
            authors = "Test Author",
            venue = "Test Venue",
            abstract = abstract,
            keywords = keywords,
            searchSources = searchSources
        )
    }

    val protocolKeywords = setOf("machine learning", "artificial intelligence", "deep learning")

    val fullMatchStudyReview = createStudyReview(
        title = "Machine Learning and Artificial Intelligence in Deep Learning",
        abstract = "This study focuses on machine learning techniques and artificial intelligence applications using deep learning approaches.",
        keywords = setOf("machine learning", "artificial intelligence", "deep learning")
    )

    val partialMatchStudyReview = createStudyReview(
        title = "Machine Learning Applications",
        abstract = "A study about machine learning in modern contexts.",
        keywords = setOf("machine learning", "applications")
    )

    val noMatchStudyReview = createStudyReview(
        title = "Software Engineering Practices",
        abstract = "Best practices in software development and testing.",
        keywords = setOf("software engineering", "testing")
    )

    val nullAbstractStudyReview = createStudyReview(
        title = "Machine Learning and Artificial Intelligence",
        abstract = null,
        keywords = setOf("machine learning", "artificial intelligence")
    )

    val repeatedKeywordsStudyReview = createStudyReview(
        title = "Machine Learning: Advanced Machine Learning with Machine Learning Techniques",
        abstract = "Machine learning is revolutionizing artificial intelligence. Machine learning applications are everywhere.",
        keywords = setOf("machine learning")
    )
}