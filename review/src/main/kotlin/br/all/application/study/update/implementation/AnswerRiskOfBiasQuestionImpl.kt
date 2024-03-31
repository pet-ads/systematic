package br.all.application.study.update.implementation

import br.all.application.question.repository.QuestionRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.interfaces.AnswerRiskOfBiasQuestionPresenter
import br.all.application.study.update.interfaces.AnswerRiskOfBiasQuestionService
import br.all.domain.model.researcher.toResearcherId
import br.all.domain.model.review.toSystematicStudyId

class AnswerRiskOfBiasQuestionServiceImpl (
    private val studyReviewRepository: StudyReviewRepository,
    private val questionRepository: QuestionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val credentialsService: ResearcherCredentialsService
):AnswerRiskOfBiasQuestionService{
    override fun answer(presenter: AnswerRiskOfBiasQuestionPresenter, request: AnswerRiskOfBiasQuestionService.RequestModel<*>) {
        val (researcher, systematicStudy, studyReviewId, questionId) = request
        PreconditionChecker(systematicStudyRepository, credentialsService).also {
            it.prepareIfViolatesPreconditions(
                presenter,
                researcher.toResearcherId(),
                systematicStudy.toSystematicStudyId()
                //não sei pq ta dando esses erros,
                // já olhei na implementação e ta como UUID
            )
        }
        if (presenter.isDone()) return

        val reviewDto = studyReviewRepository.findById(systematicStudy, studyReviewId)
        if (reviewDto == null) {
            val message = "Review with id $studyReviewId in systematic study $systematicStudy does not exist!"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }
        val updated = reviewDto.apply {
            //adicionar valores a serem modificados
        }.to(reviewDto)

       studyReviewRepository.saveOrUpdate(reviewDto)

        presenter.prepareSuccessView(
            AnswerRiskOfBiasQuestionService.ResponseModel(
                researcher,
                systematicStudy,
                studyReviewId
            )
        )
    }
}


