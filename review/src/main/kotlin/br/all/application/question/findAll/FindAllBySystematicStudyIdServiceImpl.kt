package br.all.application.question.findAll

import br.all.application.question.findAll.FindAllBySystematicStudyIdService.*
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.SystematicStudyId

class FindAllBySystematicStudyIdServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val credentialsService: CredentialsService
): FindAllBySystematicStudyIdService {
    override fun findAllBySystematicStudyId(presenter: FindAllBySystematicStudyIdPresenter, request: RequestModel) {
        val userId = request.userId
        val user = credentialsService.loadCredentials(userId)?.toUser()
        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val questions = questionRepository.findAllBySystematicStudyId(request.systematicStudyId)
        presenter.prepareSuccessView(ResponseModel(request.userId, request.systematicStudyId, questions))
    }
}