package br.all.application.report.export.service
import br.all.application.protocol.repository.PicocDto
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.question.repository.QuestionDto
import br.all.application.question.repository.QuestionRepository
import br.all.application.report.export.*
import br.all.application.report.export.presenter.ExportReviewPresenter
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.FunnelCalculator
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.SelectionStatus
import br.all.domain.shared.exception.EntityNotFoundException

class ExportReviewServiceImpl(
    private val credentialsService: CredentialsService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val questionRepository: QuestionRepository,
    private val exporters: Map<String, ReviewExporter>
) : ExportReviewService {

    override fun exportReview(presenter: ExportReviewPresenter, request: ExportReviewService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val exporter = exporters[request.format]
        if (exporter == null) {
            presenter.prepareFailView(EntityNotFoundException("Invalid export format ${request.format}"))
            return
        }

        val protocolDto = protocolRepository.findById(request.systematicStudyId)!!
        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)
        val extractionQuestions = questionRepository.findAllBySystematicStudyId(
            request.systematicStudyId, QuestionContextEnum.EXTRACTION
        )
        val robQuestions = questionRepository.findAllBySystematicStudyId(
            request.systematicStudyId, QuestionContextEnum.ROB
        )


        val excludedInScreening = allStudies.filter {
            it.selectionStatus == SelectionStatus.EXCLUDED.name
        }

        val includedInScreening = allStudies.filter {
            it.selectionStatus == SelectionStatus.INCLUDED.name
        }

        val excludedInFullText = allStudies.filter {
            it.selectionStatus == SelectionStatus.INCLUDED.name &&
                    it.extractionStatus == ExtractionStatus.EXCLUDED.name
        }

        val included = allStudies.filter {
            it.selectionStatus == SelectionStatus.INCLUDED.name &&
                    it.extractionStatus == ExtractionStatus.INCLUDED.name
        }

        val reviewData = ReviewExportData(
            systematicStudy = systematicStudyDto!!.toExportData(),
            protocol = protocolDto.toExportData(),
            studiesExcludedInScreening = excludedInScreening.map { it.toExportData(extractionQuestions, robQuestions) },
            studiesIncludedInScreening = includedInScreening.map { it.toExportData(extractionQuestions, robQuestions) },
            studiesExcludedInFullText = excludedInFullText.map { it.toExportData(extractionQuestions, robQuestions) },
            includedStudies = included.map { it.toExportData(extractionQuestions, robQuestions) },
            funnel = FunnelCalculator.calculate(allStudies)
        )

        presenter.prepareSuccessView(
            ExportReviewService.ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                format = request.format,
                formattedReview = exporter.export(reviewData)
            )
        )
    }

    private fun SystematicStudyDto.toExportData() = SystematicReviewExportData(
        id = id,
        title = title,
        description = description,
        owner = credentialsService.loadCredentials(owner)?.username ?: owner.toString(),
        collaborators = collaborators.map { credentialsService.loadCredentials(it)?.username ?: it.toString() }.toSet(),
        objectives = objectives
    )

    private fun ProtocolDto.toExportData() = ProtocolExportData(
        id = id.toString(),
        systematicStudy = systematicStudy.toString(),
        goal = goal.orEmpty(),
        justification = justification.orEmpty(),
        researchQuestions = researchQuestions.toList(),
        keywords = keywords.toList(),
        searchString = searchString.orEmpty(),
        informationSources = informationSources.toList(),
        sourcesSelectionCriteria = sourcesSelectionCriteria.orEmpty(),
        searchMethod = searchMethod.orEmpty(),
        studiesLanguages = studiesLanguages.toList(),
        studyTypeDefinition = studyTypeDefinition.orEmpty(),
        selectionProcess = selectionProcess.orEmpty(),
        eligibilityCriteria = eligibilityCriteria.map { CriterionExportData(it.description, it.type) },
        dataCollectionProcess = dataCollectionProcess.orEmpty(),
        analysisAndSynthesisProcess = analysisAndSynthesisProcess.orEmpty(),
        extractionQuestions = extractionQuestions.map {
            questionRepository.findById(systematicStudy, it)?.description ?: it.toString()
        },
        robQuestions = robQuestions.map {
            questionRepository.findById(systematicStudy, it)?.description ?: it.toString()
        },
        picoc = picoc?.toExportData()
    )

    private fun PicocDto.toExportData() = PicocExportData(
        population = population, intervention = intervention,
        control = control, outcome = outcome, context = context
    )

    private fun StudyReviewDto.toExportData(
        extractionQuestions: List<QuestionDto>,
        robQuestions: List<QuestionDto>
    ) = StudyExportData(
        id = studyReviewId,
        title = title,
        authors = authors,
        year = year,
        venue = venue,
        doi = doi,
        keywords = keywords,
        selectionCriteria = criteria,
        extractionAnswers = extractionQuestions.map {
            QuestionAnswerExportData(it.description, formAnswers[it.questionId])
        },
        robAnswers = robQuestions.map {
            QuestionAnswerExportData(it.description, robAnswers[it.questionId])
        }
    )

}
