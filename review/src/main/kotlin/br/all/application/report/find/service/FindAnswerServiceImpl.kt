package br.all.application.report.find.service

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.presenter.FindAnswerPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class FindAnswerServiceImpl(
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val collaborationRepository: CollaborationRepository
): FindAnswerService {
    override fun find(presenter: FindAnswerPresenter, request: FindAnswerService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)

        val question = questionRepository.findById(request.systematicStudyId, request.questionId)

        if (question == null) {
            val message = "Question with id ${request.questionId} not found"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        if(presenter.isDone()) return

        val questionAnswers = studyReviewRepository.findAllQuestionAnswers(request.systematicStudyId, request.questionId)

        val comparator = compareBy<String>(
            { it.toIntOrNull() == null },
            { it.toIntOrNull() ?: 0 },
            { it }
        )

        val groupedQuestionAnswers: Map<String, List<Long>> = questionAnswers
            .groupBy { it.answer }
            .mapValues { (_, list) -> list.map { it.studyReviewId } }
            .toSortedMap(comparator)

        val response = FindAnswerService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            question = question,
            answer = groupedQuestionAnswers,
        )
        presenter.prepareSuccessView(response)
    }
}