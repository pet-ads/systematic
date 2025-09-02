package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.application.protocol.find.FindProtocolStageService.RequestModel
import br.all.application.protocol.find.FindProtocolStageService.ResponseModel
import br.all.application.protocol.find.FindProtocolStageService.ProtocolStage
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.fromDto
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.review.SystematicStudy
import org.springframework.stereotype.Service

@Service
class FindProtocolStageServiceImpl(
    private val protocolRepository: ProtocolRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val questionRepository: QuestionRepository
) : FindProtocolStageService {
    override fun getStage(presenter: FindProtocolStagePresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)

        val systematicStudy = runCatching {
            systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        }.getOrNull()

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val protocolDto = protocolRepository.findById(request.systematicStudyId)
        if (protocolDto == null || systematicStudyDto == null) {
            val message = "Protocol or Systematic Study not found for id ${request.systematicStudyId}"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)
        val robQuestionsCount = questionRepository.findAllBySystematicStudyId(systematicStudyDto.id, QuestionContextEnum.ROB).size
        val extractionQuestionsCount = questionRepository.findAllBySystematicStudyId(systematicStudyDto.id, QuestionContextEnum.EXTRACTION).size

        val stage = evaluateStage(
            protocolDto,
            systematicStudyDto,
            allStudies.size,
            allStudies.count { it.selectionStatus == "INCLUDED" },
            allStudies.count { it.extractionStatus == "INCLUDED" },
            robQuestionsCount,
            extractionQuestionsCount
        )

        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, stage))
    }

    private fun evaluateStage(
        protocolDto: ProtocolDto,
        studyDto: SystematicStudyDto,
        totalStudiesCount: Int,
        includedStudiesCount: Int,
        extractedStudiesCount: Int,
        robQuestionCount: Int,
        extractionQuestionsCount: Int
    ): ProtocolStage {
        return when {
            !isT1Complete(studyDto, protocolDto) -> ProtocolStage.GENERAL_DEFINITION
            !isT2Complete(protocolDto) -> ProtocolStage.RESEARCH_QUESTIONS

            isPicocStarted(protocolDto) && !isT3Complete(protocolDto) -> ProtocolStage.PICOC

            !isT4Complete(protocolDto) -> ProtocolStage.ELIGIBILITY_CRITERIA
            !isT5Complete(protocolDto) -> ProtocolStage.INFORMATION_SOURCES_AND_SEARCH_STRATEGY
            !isT6Complete(protocolDto, extractionQuestionsCount) -> ProtocolStage.SELECTION_AND_EXTRACTION

            !isT8Complete(protocolDto) -> ProtocolStage.ANALYSIS_AND_SYNTHESIS_METHOD

            totalStudiesCount == 0 -> ProtocolStage.IDENTIFICATION
            includedStudiesCount == 0 -> ProtocolStage.SELECTION
            extractedStudiesCount == 0 -> ProtocolStage.EXTRACTION
            else -> ProtocolStage.GRAPHICS
        }
    }


    private fun isT1Complete(studyDto: SystematicStudyDto, protocolDto: ProtocolDto): Boolean {
        val isStudyInfoComplete = studyDto.title.isNotBlank() && studyDto.description.isNotBlank()
        val isProtocolGoalComplete = !protocolDto.goal.isNullOrBlank()
        return isStudyInfoComplete && isProtocolGoalComplete
    }

    private fun isT2Complete(dto: ProtocolDto): Boolean {
        return dto.researchQuestions.isNotEmpty()
    }

    private fun isPicocStarted(dto: ProtocolDto): Boolean {
        val picoc = dto.picoc ?: return false
        return !picoc.population.isNullOrBlank() || !picoc.intervention.isNullOrBlank() ||
                !picoc.control.isNullOrBlank() || !picoc.outcome.isNullOrBlank() || !picoc.context.isNullOrBlank()
    }

    private fun isT3Complete(dto: ProtocolDto): Boolean {
        val picoc = dto.picoc ?: return false

        return !picoc.population.isNullOrBlank() &&
                !picoc.intervention.isNullOrBlank() &&
                !picoc.control.isNullOrBlank() &&
                !picoc.outcome.isNullOrBlank()
    }

    private fun isT4Complete(dto: ProtocolDto): Boolean {
        val hasInclusion = dto.eligibilityCriteria.any { it.type.equals("INCLUSION", ignoreCase = true) }
        val hasExclusion = dto.eligibilityCriteria.any { it.type.equals("EXCLUSION", ignoreCase = true) }

        val hasStudyType = !dto.studyTypeDefinition.isNullOrBlank()
        val hasLanguage = dto.studiesLanguages.isNotEmpty()

        return hasInclusion && hasExclusion && hasStudyType && hasLanguage
    }

    private fun isT5Complete(dto: ProtocolDto): Boolean {
        return !dto.sourcesSelectionCriteria.isNullOrBlank() &&
                dto.informationSources.isNotEmpty() &&
                !dto.searchMethod.isNullOrBlank() &&
                dto.keywords.isNotEmpty() &&
                !dto.searchString.isNullOrBlank()
    }

    private fun isT6Complete(dto: ProtocolDto, extractionQuestionsCount: Int): Boolean {
        return !dto.selectionProcess.isNullOrBlank() &&
                !dto.dataCollectionProcess.isNullOrBlank() &&
                extractionQuestionsCount > 0
    }

    // T7 - Risk Of Bias
    // Não é necessária uma função 'isT7Complete' na lógica principal, pois
    // a regra é: se não houver questões, o sistema avança para T8.
    // A presença de questões (robQuestionCount > 0) simplesmente marca a tarefa como feita.

    private fun isT8Complete(dto: ProtocolDto): Boolean {
        return !dto.analysisAndSynthesisProcess.isNullOrBlank()
    }
}