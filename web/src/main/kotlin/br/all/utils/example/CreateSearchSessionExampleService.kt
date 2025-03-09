package br.all.utils.example

import br.all.application.search.repository.SearchSessionDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.toDto
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.toSearchSessionID
import br.all.domain.services.ConverterFactoryService
import br.all.domain.services.UuidGeneratorService
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.time.LocalDateTime
import java.util.*

@Service
class CreateSearchSessionExampleService (
    private val converterFactoryService: ConverterFactoryService,
    private val uuidGeneratorService: UuidGeneratorService,
    private val studyReviewRepository: StudyReviewRepository,
    private val searchSessionRepository: SearchSessionRepository,
) {
    fun convert(
        systematicStudyId: SystematicStudyId,
        userId: UUID,
        bibFileName: String,
        sourceName: String,
        timestamp: LocalDateTime,
        searchString: String,
        additionalInformation: String
    ) {
        val search = uuidGeneratorService.next()
        val resource = ClassPathResource(bibFileName)

        val fileContent = resource.inputStream.bufferedReader().use(BufferedReader::readText)
        val (studyReviews) = converterFactoryService.extractReferences(
            systematicStudyId, search.toSearchSessionID(), fileContent
        )

        val searchSession = SearchSessionDto(
            id = uuidGeneratorService.next(),
            systematicStudyId = systematicStudyId.value(),
            userId = userId,
            searchString = searchString,
            additionalInfo = additionalInformation,
            timestamp = timestamp,
            source = sourceName,
            numberOfRelatedStudies = studyReviews.size,
        )
        studyReviewRepository.saveOrUpdateBatch(studyReviews.map { it.toDto() })
        searchSessionRepository.create(searchSession)
    }
}
