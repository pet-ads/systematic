package br.all.application.question.find

import br.all.application.question.find.FindQuestionService.*
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.domain.shared.exception.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudy

class FindQuestionServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val credentialsService: CredentialsService
) : FindQuestionService {

    override fun findOne(presenter: FindQuestionPresenter, request: RequestModel) {
        val userId = request.userId
        val user = credentialsService.loadCredentials(userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val questionId = QuestionId(request.questionId)
        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val question = questionRepository.findById(request.systematicStudyId, questionId.value)
        if (question === null) {
            val message =
                "There is no review of id ${request.systematicStudyId} or question of id ${request.questionId}."
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }
        presenter.prepareSuccessView(ResponseModel(request.userId, question))
    }
}
