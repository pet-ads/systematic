package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.application.protocol.find.GetProtocolStageService.RequestModel
import br.all.application.protocol.find.GetProtocolStageService.ResponseModel
import br.all.application.protocol.find.GetProtocolStageService.ProtocolStage
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.review.SystematicStudy
import org.springframework.stereotype.Service

@Service
class GetProtocolStageServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService
) : GetProtocolStageService {
    override fun getStage(presenter: GetProtocolStagePresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val protocolDto = protocolRepository.findById(request.systematicStudyId)
        if (protocolDto == null) {
            val message = "Protocol not found for systematic study ${request.systematicStudyId}"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)

        val totalStudiesCount = allStudies.size
        val includedStudiesCount = allStudies.count { it.selectionStatus == "INCLUDED" }
        val extractedStudiesCount = allStudies.count { it.extractionStatus == "INCLUDED" }

        val stage = evaluateStage(protocolDto, totalStudiesCount, includedStudiesCount, extractedStudiesCount)

        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, stage))
    }


    private fun evaluateStage(dto: ProtocolDto, totalStudiesCount: Int, includedStudiesCount: Int, extractedStudiesCount: Int) : ProtocolStage {
        if (dto.goal.isNullOrBlank() && dto.justification.isNullOrBlank()) {
            return ProtocolStage.PROTOCOL_PART_I
        }

        val picoc = dto.picoc
        val picocIsStarted = picoc != null && (
                    !picoc.population.isNullOrBlank() || !picoc.intervention.isNullOrBlank() ||
                    !picoc.control.isNullOrBlank() || !picoc.outcome.isNullOrBlank() || !picoc.context.isNullOrBlank()
                )

        if (picocIsStarted) {
            val picocIsCompleted = !picoc!!.population.isNullOrBlank() && !picoc.intervention.isNullOrBlank() &&
                    !picoc.control.isNullOrBlank() && !picoc.outcome.isNullOrBlank() && !picoc.context.isNullOrBlank()

            if (!picocIsCompleted) {
                return ProtocolStage.PICOC
            }
        }

        if (dto.studiesLanguages.isEmpty() && dto.eligibilityCriteria.isEmpty() &&
            dto.informationSources.isEmpty() && dto.keywords.isEmpty() &&
            dto.sourcesSelectionCriteria.isNullOrBlank() && dto.searchMethod.isNullOrBlank() &&
            dto.selectionProcess.isNullOrBlank()) {
            return ProtocolStage.PROTOCOL_PART_II
        }

        val hasInclusionCriteria = dto.eligibilityCriteria.any { it.type.equals("INCLUSION", true) }
        val hasExclusionCriteria = dto.eligibilityCriteria.any { it.type.equals("EXCLUSION", true) }

        val isSetupComplete = dto.extractionQuestions.isNotEmpty() &&
                dto.robQuestions.isNotEmpty() &&
                hasInclusionCriteria &&
                hasExclusionCriteria &&
                dto.informationSources.isNotEmpty()

        val areFinalFieldsFilled =  dto.researchQuestions.isNotEmpty() && dto.analysisAndSynthesisProcess.isNullOrBlank()

        if (!isSetupComplete || !areFinalFieldsFilled) {
            return ProtocolStage.PROTOCOL_PART_III
        }

        if (totalStudiesCount == 0) {
            return ProtocolStage.IDENTIFICATION
        }

        if (includedStudiesCount == 0) {
            return ProtocolStage.SELECTION
        }

        if (extractedStudiesCount == 0) {
            return ProtocolStage.EXTRACTION
        }

        // This stage is reached when extraction is complete, but finalization has not yet begun.
        // As Finalization criteria are not yet defined, this is the default final step.
        return ProtocolStage.GRAPHICS

        // Finalization would be returned here once its criteria are defined.
    }
}