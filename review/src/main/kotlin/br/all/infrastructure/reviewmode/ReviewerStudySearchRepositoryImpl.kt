package br.all.infrastructure.reviewmode

import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudySearchRepository
import br.all.domain.model.study.StudyReviewStage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class ReviewerStudySearchRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : ReviewerStudySearchRepository {

    override fun findAllByAdvancedSearch(
        systematicStudyId: UUID,
        reviewerId: UUID,
        stage: StudyReviewStage?,
        filters: Map<String, Any?>,
        pageable: Pageable,
    ): Page<ReviewerStudyDto> {
        val criteria = mutableListOf<Criteria>()
        criteria += Criteria.where("_id.systematicStudyId").`is`(systematicStudyId)
        criteria += Criteria.where("_id.researcherId").`is`(reviewerId)
        stage?.let { criteria += Criteria.where("_id.stage").`is`(it.name) }

        filters["id"]?.let { criteria += Criteria.where("_id.studyId").`is`(it) }
        listOf("title", "authors", "venue").forEach { field ->
            filters[field]?.let {
                criteria += Criteria.where("studyReview.$field").regex(".*${Regex.escape(it as String)}.*", "i")
            }
        }
        listOf("year", "selectionStatus", "extractionStatus", "score", "readingPriority").forEach { field ->
            filters[field]?.let { criteria += Criteria.where("studyReview.$field").`is`(it) }
        }

        val query = Query().addCriteria(Criteria().andOperator(*criteria.toTypedArray()))
        val total = mongoTemplate.count(query, ReviewerStudyDocument::class.java)

        query.with(pageable.toDocumentPageable())
        val content = mongoTemplate.find(query, ReviewerStudyDocument::class.java).map { it.toDto() }

        return PageImpl(content, pageable, total)
    }

    private fun Pageable.toDocumentPageable(): Pageable {
        if (isUnpaged) return this
        val orders = sort.map { order ->
            order.withProperty(if (order.property == "id") "id.studyId" else "studyReview.${order.property}")
        }.toList()
        return PageRequest.of(pageNumber, pageSize, Sort.by(orders))
    }
}