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
import br.all.application.shared.presenter.AnswerAggregator
import br.all.application.shared.presenter.CriteriaGrouping
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.study.ExtractionStatus
import br.all.domain.model.study.SelectionStatus
import br.all.domain.model.study.StudyReviewStage
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

        val conduction = buildConductionSections(request, allStudies, extractionQuestions, robQuestions)
        val exportItems = buildExportItems(request, allStudies, extractionQuestions + robQuestions)

        val reviewData = ReviewExportData(
            systematicStudy = systematicStudyDto!!.toExportData(),
            protocol = protocolDto.toExportData(),
            conduction = conduction,
            exportItems = exportItems
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

    private fun buildConductionSections(
        request: ExportReviewService.RequestModel,
        allStudies: List<StudyReviewDto>,
        extractionQuestions: List<QuestionDto>,
        robQuestions: List<QuestionDto>
    ): ConductionExportSections {
        val config = request.conduction
        val id = request.systematicStudyId

        val includedFirstSelection = allStudies.filter { it.selectionStatus == SelectionStatus.INCLUDED.name }
        val excludedFirstSelection = allStudies.filter { it.selectionStatus == SelectionStatus.EXCLUDED.name }
        val includedSecondSelection = allStudies.filter {
            it.selectionStatus == SelectionStatus.INCLUDED.name && it.extractionStatus == ExtractionStatus.INCLUDED.name
        }
        val excludedSecondSelection = allStudies.filter {
            it.selectionStatus == SelectionStatus.INCLUDED.name && it.extractionStatus == ExtractionStatus.EXCLUDED.name
        }

        return ConductionExportSections(
            includedInFirstSelection = if (config.includedInFirstSelection)
                CriteriaGrouping.groupByCriterion(protocolRepository, id, "INCLUSION", StudyReviewStage.SELECTION, includedFirstSelection)
            else null,
            excludedInFirstSelection = if (config.excludedInFirstSelection)
                CriteriaGrouping.groupByCriterion(protocolRepository, id, "EXCLUSION", StudyReviewStage.SELECTION, excludedFirstSelection)
            else null,
            includedInSecondSelection = if (config.includedInSecondSelection)
                CriteriaGrouping.groupByCriterion(protocolRepository, id, "INCLUSION", StudyReviewStage.EXTRACTION, includedSecondSelection)
            else null,
            excludedInSecondSelection = if (config.excludedInSecondSelection)
                CriteriaGrouping.groupByCriterion(protocolRepository, id, "EXCLUSION", StudyReviewStage.EXTRACTION, excludedSecondSelection)
            else null,
            consolidatedExtraction = if (config.consolidatedExtraction)
                includedSecondSelection.map { it.toExportData(extractionQuestions, robQuestions) }
            else null,
            funnelImageFileName = if (config.funnel) "StudiesFunnel.png" else null
        )
    }

    private fun buildExportItems(
        request: ExportReviewService.RequestModel,
        allStudies: List<StudyReviewDto>,
        allQuestions: List<QuestionDto>
    ): List<ExportItemExportData> {
        val includedIds = allStudies.filter {
            it.selectionStatus == SelectionStatus.INCLUDED.name && it.extractionStatus == ExtractionStatus.INCLUDED.name
        }.map { it.studyReviewId }.toSet()
        val studiesById = allStudies.associateBy { it.studyReviewId }

        return request.exportItems.map { item ->
            val question = allQuestions.find { it.questionId == item.questionId }
            val description = question?.description ?: item.questionId.toString()

            val answers = studyReviewRepository
                .findAllQuestionAnswers(request.systematicStudyId, item.questionId)
                .filter { it.studyReviewId in includedIds }

            val exploded: List<Pair<String, Long>> = if (question?.questionType == "PICK_MANY") {
                answers.flatMap { a -> AnswerAggregator.parsePickManyLabel(a.answer).map { it to a.studyReviewId } }
            } else {
                answers.map { a -> AnswerAggregator.formatAnswerLabel(a.answer) to a.studyReviewId }
            }

            when (item.visualization) {
                VisualizationType.TABLE -> {
                    if (question?.questionType == "TEXTUAL") {
                        val rows = answers.mapNotNull { a ->
                            studiesById[a.studyReviewId]?.let {
                                TextualTableRow(a.studyReviewId, it.title, it.authors, a.answer)
                            }
                        }
                        ExportItemExportData(item.questionId, description, item.visualization,
                            textualTable = TextualTableExportData(rows))
                    } else {
                        val total = exploded.size
                        val rows = exploded.groupBy({ it.first }, { it.second }).map { (answer, ids) ->
                            GroupedTableRow(answer, ids, ids.size, if (total > 0) ids.size * 100.0 / total else 0.0)
                        }
                        ExportItemExportData(item.questionId, description, item.visualization, tableRows = rows)
                    }
                }

                VisualizationType.ITEM_TABLE -> {
                    val options = question?.options.orEmpty()
                    val rows = answers.map { a ->
                        val selected = AnswerAggregator.parsePickManyLabel(a.answer).toSet()
                        ItemTableRow(a.studyReviewId, options.associateWith { it in selected })
                    }
                    ExportItemExportData(item.questionId, description, item.visualization,
                        itemTable = ItemTableExportData(options, rows))
                }

                VisualizationType.PIE_CHART, VisualizationType.BAR_CHART, VisualizationType.LINE_CHART -> {
                    val grouped = exploded.groupBy({ it.first }, { it.second })
                    val labels = grouped.keys.toList()
                    val values = grouped.values.map { it.size }
                    ExportItemExportData(item.questionId, description, item.visualization,
                        pieBarData = PieBarChartExportData(labels, values))
                }

                VisualizationType.BUBBLE_CHART -> {
                    val points = exploded
                        .mapNotNull { (label, studyId) -> studiesById[studyId]?.year?.let { year -> Triple(year, label, studyId) } }
                        .groupBy { it.first to it.second }
                        .map { (key, list) -> BubbleChartPointExportData(key.first, key.second, list.size) }
                    ExportItemExportData(item.questionId, description, item.visualization,
                        bubbleData = BubbleChartExportData(points))
                }
            }
        }
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
        type = studyType,
        bases = searchSources,
        keywords = keywords,
        selectionCriteria = selectionCriteria,
        extractionAnswers = extractionQuestions.map {
            QuestionAnswerExportData(it.description, formAnswers[it.questionId])
        },
        robAnswers = robQuestions.map {
            QuestionAnswerExportData(it.description, robAnswers[it.questionId])
        }
    )
}