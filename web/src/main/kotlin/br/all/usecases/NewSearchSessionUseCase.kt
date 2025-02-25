package br.all.usecases

import br.all.application.search.repository.SearchSessionDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.toDto
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.toSearchSessionID
import br.all.domain.services.ConverterFactoryService
import br.all.domain.services.UuidGeneratorService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class NewSearchSessionUseCase (
    private val converterFactoryService: ConverterFactoryService,
    private val uuidGeneratorService: UuidGeneratorService,
    private val studyReviewRepository: StudyReviewRepository,
    private val searchSessionRepository: SearchSessionRepository,
) {
    fun convert(
        systematicStudyId: SystematicStudyId,
        userId: UUID,
    ) {
        val search = uuidGeneratorService.next()
        val fileContent = object {}.javaClass.getResource("/ALL.bib")?.readText()
            ?: throw IllegalStateException("Arquivo ALL.bib não encontrado no classpath")
        val (studyReviews) = converterFactoryService.extractReferences(
            systematicStudyId, search.toSearchSessionID(), fileContent
        )

        val searchSession = SearchSessionDto(
            id = uuidGeneratorService.next(),
            systematicStudyId = systematicStudyId.value(),
            userId = userId,
            searchString = "",
            additionalInfo = null,
            timestamp = LocalDateTime.now(),
            source = "Scopus",
            numberOfRelatedStudies = studyReviews.size,
        )
        studyReviewRepository.saveOrUpdateBatch(studyReviews.map { it.toDto() })
        searchSessionRepository.create(searchSession)
    }
}
