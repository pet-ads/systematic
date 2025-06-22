package br.all.infrastructure.study

import br.all.application.study.repository.AnswerDto
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
open class  StudyReviewRepositoryImpl(private val repository: MongoStudyReviewRepository) : StudyReviewRepository {
    override fun saveOrUpdate(dto: StudyReviewDto): StudyReviewDocument = repository.save(dto.toDocument())

    override fun findById(reviewId: UUID, studyId: Long) =
        repository.findById(StudyReviewId(reviewId, studyId)).toNullable()?.toDto()

    override fun findAllFromReview(reviewId: UUID): List<StudyReviewDto> =
        repository.findAllById_SystematicStudyId(reviewId).map { it.toDto() }

    override fun findAllBySource(reviewId: UUID, source: String): List<StudyReviewDto> =
//        repository.findAllById_SystematicStudyId(reviewId).map { it.toDto() }
        repository.findAllById_SystematicStudyIdAndSearchSourcesContaining(reviewId, source).map { it.toDto() }

    override fun findAllBySession(reviewId: UUID, searchSessionId: UUID): List<StudyReviewDto> =
        repository.findAllById_SystematicStudyIdAndSearchSessionId(reviewId, searchSessionId).map { it.toDto() }


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
}

