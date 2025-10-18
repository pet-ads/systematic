package br.all.application.question.delete

import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.question.QuestionId
import br.all.domain.model.review.SystematicStudy
import org.springframework.stereotype.Service

@Service
class DeleteQuestionServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val credentialsService: CredentialsService,
) : DeleteQuestionService {
    override fun delete(presenter: DeleteQuestionPresenter, request: DeleteQuestionService.RequestModel) {
        val systematicStudyId = request.systematicStudyId
        val userId = request.userId

        val user = credentialsService.loadCredentials(userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)
        if (presenter.isDone()) return

        val questionId = QuestionId(request.questionId)
        val question = questionRepository.findById(systematicStudyId, questionId.value)
        if (question == null) {
            presenter.prepareFailView(NoSuchElementException("Question with id ${questionId.value} not found for study $systematicStudyId."))
            return
        }

        try {
            questionRepository.deleteById(systematicStudyId, questionId.value)
        } catch (e: Exception) {
            presenter.prepareFailView(e)
            return
        }

        presenter.prepareSuccessView(DeleteQuestionService.ResponseModel(userId, systematicStudyId, question.questionId))
    }
}