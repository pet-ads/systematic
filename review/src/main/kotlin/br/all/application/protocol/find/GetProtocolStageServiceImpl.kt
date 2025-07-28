package br.all.application.protocol.find

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.application.protocol.find.GetProtocolStageService.RequestModel
import br.all.application.protocol.find.GetProtocolStageService.ResponseModel
import br.all.application.protocol.find.GetProtocolStageService.ProtocolStage
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.review.SystematicStudy
import org.springframework.stereotype.Service

@Service
class GetProtocolStageServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val questionRepository: QuestionRepository,
    private val collaborationRepository: CollaborationRepository,
) : GetProtocolStageService {
    override fun getStage(presenter: GetProtocolStagePresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)
        if (presenter.isDone()) return

        val protocolDto = protocolRepository.findById(request.systematicStudyId)
        if (protocolDto == null) {
            val message = "Protocol not found for systematic study ${request.systematicStudyId}"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)
        val robQuestions = questionRepository.findAllBySystematicStudyId(systematicStudyDto!!.id, QuestionContextEnum.ROB).size
        val extractionQuestions = questionRepository.findAllBySystematicStudyId(systematicStudyDto.id, QuestionContextEnum.EXTRACTION).size

        val totalStudiesCount = allStudies.size
        val includedStudiesCount = allStudies.count { it.selectionStatus == "INCLUDED" }
        val extractedStudiesCount = allStudies.count { it.extractionStatus == "INCLUDED" }

        val stage = evaluateStage(
            protocolDto,
            totalStudiesCount,
            includedStudiesCount,
            extractedStudiesCount,
            robQuestions,
            extractionQuestions,
        )

        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, stage))
    }

    private fun evaluateStage(
        dto: ProtocolDto,
        totalStudiesCount: Int,
        includedStudiesCount: Int,
        extractedStudiesCount: Int,
        robQuestionCount: Int,
        extractionQuestionsCount: Int
    ): ProtocolStage {
        return when {
            isProtocolPartI(dto) -> ProtocolStage.PROTOCOL_PART_I
            picocStage(dto) -> ProtocolStage.PICOC
            isProtocolPartII(dto) -> ProtocolStage.PROTOCOL_PART_II
            !isProtocolPartIIICompleted(dto, robQuestionCount, extractionQuestionsCount) -> ProtocolStage.PROTOCOL_PART_III
            totalStudiesCount == 0 -> ProtocolStage.IDENTIFICATION
            includedStudiesCount == 0 -> ProtocolStage.SELECTION
            extractedStudiesCount == 0 -> ProtocolStage.EXTRACTION
            else -> ProtocolStage.GRAPHICS
        }
    }

    private fun isProtocolPartI(dto: ProtocolDto): Boolean {
        return dto.goal.isNullOrBlank() && dto.justification.isNullOrBlank()
    }

    private fun isProtocolPartII(dto: ProtocolDto): Boolean {
        return dto.studiesLanguages.isEmpty() &&
                dto.eligibilityCriteria.isEmpty() &&
                dto.informationSources.isEmpty() &&
                dto.keywords.isEmpty() &&
                dto.sourcesSelectionCriteria.isNullOrBlank() &&
                dto.searchMethod.isNullOrBlank() &&
                dto.selectionProcess.isNullOrBlank()
    }

    private fun isProtocolPartIIICompleted(dto: ProtocolDto, robQuestionCount: Int, extractionQuestionsCount: Int): Boolean {
        val hasInclusionCriteria = dto.eligibilityCriteria.any { it.type.equals("INCLUSION", ignoreCase = true) }
        val hasExclusionCriteria = dto.eligibilityCriteria.any { it.type.equals("EXCLUSION", ignoreCase = true) }

        val hasExtractionAndRob = robQuestionCount > 0 && extractionQuestionsCount > 0

        val hasDatabases = dto.informationSources.isNotEmpty()
        val hasResearchQuestions = dto.researchQuestions.isNotEmpty()
        val hasAnalysisProcess = !dto.analysisAndSynthesisProcess.isNullOrBlank()

        return hasInclusionCriteria && hasExclusionCriteria &&
                hasExtractionAndRob && hasDatabases &&
                hasResearchQuestions && hasAnalysisProcess
    }

    private fun picocStage(dto: ProtocolDto): Boolean {
        val picoc = dto.picoc
        if (picoc == null) return false

        val picocIsStarted = !picoc.population.isNullOrBlank() || !picoc.intervention.isNullOrBlank() ||
                !picoc.control.isNullOrBlank() || !picoc.outcome.isNullOrBlank() || !picoc.context.isNullOrBlank()

        if (picocIsStarted) {
            val picocIsCompleted = !picoc.population.isNullOrBlank() && !picoc.intervention.isNullOrBlank() &&
                    !picoc.control.isNullOrBlank() && !picoc.outcome.isNullOrBlank() && !picoc.context.isNullOrBlank()

            if (!picocIsCompleted) {
                return true
            }
        }

        return false
    }
}
