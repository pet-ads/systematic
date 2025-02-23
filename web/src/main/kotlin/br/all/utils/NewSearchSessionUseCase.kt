package br.all.utils

import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.toDto
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.toSearchSessionID
import br.all.domain.services.ConverterFactoryService
import br.all.domain.services.UuidGeneratorService
import org.springframework.stereotype.Service
import java.io.File

@Service
class NewSearchSessionUseCase (
    private val converterFactoryService: ConverterFactoryService,
    private val uuidGeneratorService: UuidGeneratorService,
    private val studyReviewRepository: StudyReviewRepository,
) {
    fun convert(
        systematicStudyId: SystematicStudyId,
    ) {
        val search = uuidGeneratorService.next()
        val file = File("/Users/erickgomes/Documents/PET-ADS/test/systematic/web/src/main/kotlin/br/all/utils/ALL.bib").readText()
        val (studyReviews, invalidEntries) = converterFactoryService.extractReferences(systematicStudyId, search.toSearchSessionID(), file)

        studyReviewRepository.saveOrUpdateBatch(studyReviews.map { it.toDto() })
    }
}