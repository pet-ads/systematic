package br.all.application.question.findAll

import br.all.application.collaboration.repository.CollaborationRepository
import br.all.application.collaboration.repository.toDomain
import br.all.application.question.findAll.FindAllBySystematicStudyIdService.*
import br.all.application.question.repository.QuestionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.user.CredentialsService
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.review.SystematicStudy

class FindAllBySystematicStudyIdServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val questionRepository: QuestionRepository,
    private val credentialsService: CredentialsService,
    private val collaborationRepository: CollaborationRepository
) : FindAllBySystematicStudyIdService {

    override fun findAllBySystematicStudyId(presenter: FindAllBySystematicStudyIdPresenter, request: RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }
        val collaborations = collaborationRepository
            .listAllCollaborationsBySystematicStudyId(request.systematicStudyId)
            .map { it.toDomain() }

        presenter.prepareIfFailsPreconditions(user, systematicStudy, collaborations = collaborations)
        if (presenter.isDone()) return

        val questions = questionRepository.findAllBySystematicStudyId(
            request.systematicStudyId,
            request.context?.let { QuestionContextEnum.valueOf(it) }
        )

        presenter.prepareSuccessView(
            ResponseModel(
                userId = request.userId,
                systematicStudyId = request.systematicStudyId,
                questions = questions
            )
        )
    }
}