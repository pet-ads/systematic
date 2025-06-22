package br.all.utils.example

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.search.repository.SearchSessionDto
import br.all.application.search.repository.SearchSessionRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.toDto
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.toSearchSessionID
import br.all.domain.services.ConverterFactoryService
import br.all.domain.services.ReviewSimilarityService
import br.all.domain.services.ScoreCalculatorService
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
    private val protocolRepository: ProtocolRepository,
    private val scoreCalculatorService: ScoreCalculatorService,
    private val reviewSimilarityService: ReviewSimilarityService
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
            systematicStudyId, search.toSearchSessionID(), fileContent,
            mutableSetOf(sourceName),
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

        val protocolDto = protocolRepository.findById(systematicStudyId.value())

        val scoredStudyReviews = scoreCalculatorService.applyScoreToManyStudyReviews(studyReviews, protocolDto!!.keywords)

        studyReviewRepository.saveOrUpdateBatch(scoredStudyReviews.map { it.toDto() })

        val duplicatedAnalysedReviews = reviewSimilarityService.findDuplicates(scoredStudyReviews, emptyList())
        val toSaveDuplicatedAnalysedReviews = duplicatedAnalysedReviews
            .flatMap { (key, value) -> listOf(key) + value }
            .toList()

        studyReviewRepository.saveOrUpdateBatch(toSaveDuplicatedAnalysedReviews.map { it.toDto() })

        searchSessionRepository.create(searchSession)
    }
}
