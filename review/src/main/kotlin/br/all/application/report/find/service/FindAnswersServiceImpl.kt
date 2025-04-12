package br.all.application.report.find.service

import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.presenter.FindAnswersPresenter
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.services.IdGeneratorService
import java.util.*

class FindAnswersServiceImpl(
    private val questionRepository: QuestionRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val idGenerator: IdGeneratorService
): FindAnswersService {

    override fun findAnswers(presenter: FindAnswersPresenter, request: FindAnswersService.RequestModel) {

        val response = FindAnswersService.ResponseModel<String>(
            userId = UUID.randomUUID(),
            systematicStudyId = UUID.randomUUID(),
            studyReviewId = 11111,
            questionId = UUID.randomUUID(),
            code = "Test",
            description = "Description of test",
            type = "TEXTUAL",
            answer = "Funcionou!!!",
        )
        presenter.prepareSuccessView(response)
    }
}
