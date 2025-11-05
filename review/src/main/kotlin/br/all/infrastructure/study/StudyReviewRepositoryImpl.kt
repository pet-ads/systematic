package br.all.infrastructure.study

import br.all.application.study.repository.AnswerDto
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.study.SelectionStatus
import br.all.infrastructure.shared.toNullable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class  StudyReviewRepositoryImpl(private val repository: MongoStudyReviewRepository,
                                      private val mongoTemplate: MongoTemplate
) : StudyReviewRepository {
    override fun saveOrUpdate(dto: StudyReviewDto): StudyReviewDocument = repository.save(dto.toDocument())

    override fun findById(reviewId: UUID, studyId: Long) =
        repository.findById(StudyReviewId(reviewId, studyId)).toNullable()?.toDto()

    override fun findAllFromReview(reviewId: UUID, pageable: Pageable): List<StudyReviewDto> =
        repository.findAllById_SystematicStudyId(reviewId, pageable).content.map { it.toDto() }
        
    override fun findAllFromReviewPaged(reviewId: UUID, pageable: Pageable): Page<StudyReviewDto> {
        val documentsPage = repository.findAllById_SystematicStudyId(reviewId, pageable)
        return documentsPage.map { it.toDto() }
    }

    override fun findAllBySource(reviewId: UUID, source: String): List<StudyReviewDto> =
//        repository.findAllById_SystematicStudyId(reviewId).map { it.toDto() }
        repository.findAllById_SystematicStudyIdAndSearchSourcesContaining(reviewId, source).map { it.toDto() }

    override fun findAllBySession(reviewId: UUID, searchSessionId: UUID): List<StudyReviewDto> =
        repository.findAllById_SystematicStudyIdAndSearchSessionId(reviewId, searchSessionId).map { it.toDto() }

    override fun findAllBySessionPaged(
        reviewId: UUID,
        searchSessionId: UUID,
        pageable: Pageable
    ): Page<StudyReviewDto> {
        val documentsPage = repository.findAllById_SystematicStudyIdAndSearchSessionId(reviewId, searchSessionId, pageable)
        return documentsPage.map { it.toDto() }
    }

    override fun findAllBySystematicStudyIdAndSelectionStatusPaged(
        reviewId: UUID,
        status: SelectionStatus,
        pageable: Pageable
    ): Page<StudyReviewDto> {
        val documentsPage = repository.findAllById_SystematicStudyIdAndSelectionStatus(
            reviewId,
            status,
            pageable
        )
        return documentsPage.map { it.toDto() }
    }

    override fun updateSelectionStatus(reviewId: UUID, studyId: Long, attributeName: String, newStatus: Any) {
        repository.findAndUpdateAttributeById(StudyReviewId(reviewId, studyId), attributeName, newStatus)
    }

    override fun saveOrUpdateBatch(dtos: List<StudyReviewDto>) {
        repository.saveAll(dtos.map { it.toDocument() })
    }

    override fun findAllQuestionAnswers(reviewId: UUID, questionId: UUID): List<AnswerDto> =
        repository
            .findAllAnswersForQuestion(questionId.toString())
            .map { infraDto ->
                AnswerDto(
                    studyReviewId   = infraDto.studyReviewId,
                    answer          = infraDto.answer
                )
            }

    override fun findAllByAdvancedSearch(
        reviewId: UUID,
        filters: Map<String, Any?>,
        pageable: Pageable
    ): Page<StudyReviewDto> {
        val criteria = mutableListOf<Criteria>()
        criteria += Criteria.where("_id.systematicStudyId").`is`(reviewId)

        filters["id"]?.let { criteria += Criteria.where("_id.studyReviewId").`is`(it) }
        filters["studyReviewId"]?.let {
            criteria += Criteria.where("studyReviewId").regex(".*${Regex.escape(it as String)}.*", "i")
        }
        filters["title"]?.let {
            criteria += Criteria.where("title").regex(".*${Regex.escape(it as String)}.*", "i")
        }
        filters["authors"]?.let {
            criteria += Criteria.where("authors").regex(".*${Regex.escape(it as String)}.*", "i")
        }
        filters["venue"]?.let {
            criteria += Criteria.where("venue").regex(".*${Regex.escape(it as String)}.*", "i")
        }
        filters["year"]?.let {
            criteria += Criteria.where("year").`is`(it)
        }
        filters["selectionStatus"]?.let {
            criteria += Criteria.where("selectionStatus").`is`(it)
        }
        filters["extractionStatus"]?.let {
            criteria += Criteria.where("extractionStatus").`is`(it)
        }
        filters["score"]?.let {
            criteria += Criteria.where("score").`is`(it)
        }
        filters["readingPriority"]?.let {
            criteria += Criteria.where("readingPriority").`is`(it)
        }

        val query = Query().addCriteria(Criteria().andOperator(*criteria.toTypedArray()))
        query.with(pageable)

        val total = mongoTemplate.count(query, StudyReviewDocument::class.java)
        val documents = mongoTemplate.find(query, StudyReviewDocument::class.java)

        val content = documents.map { it.toDto() }

        return PageImpl(content, pageable, total)
    }
}

