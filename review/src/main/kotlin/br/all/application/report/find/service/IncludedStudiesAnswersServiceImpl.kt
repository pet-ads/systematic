package br.all.application.report.find.service

import br.all.application.question.repository.QuestionRepository
import br.all.application.report.find.presenter.IncludedStudiesAnswersPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.model.review.SystematicStudy

class IncludedStudiesAnswersServiceImpl(
    private val questionRepository: QuestionRepository,
    private val studyReviewRepository: StudyReviewRepository,
    private val credentialsService: CredentialsService,
    private val systematicStudyRepository: SystematicStudyRepository,
) : IncludedStudiesAnswersService {

    override fun findAnswers(presenter: IncludedStudiesAnswersPresenter, request: IncludedStudiesAnswersService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if (presenter.isDone()) return

        val studyReviewDto = studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId)

        if (studyReviewDto == null) {
            val message = "StudyReview not found for id ${request.systematicStudyId}"
            presenter.prepareFailView(EntityNotFoundException(message))
            return
        }

        val extractionQuestions = questionRepository
            .findAllBySystematicStudyId(request.systematicStudyId, QuestionContextEnum.EXTRACTION)
        val extractionAnswers = studyReviewDto.formAnswers

        val robQuestions =
            questionRepository.findAllBySystematicStudyId(request.systematicStudyId, QuestionContextEnum.ROB)
        val robAnswers = studyReviewDto.robAnswers

        val extractionResponse: List<IncludedStudiesAnswersService.QuestionWithAnswer> = extractionQuestions.map { q ->
            IncludedStudiesAnswersService.QuestionWithAnswer(
                questionId = q.questionId,
                code = q.code,
                type = q.questionType,
                description = q.description,
                answer = extractionAnswers[q.questionId]
            )
        }

        val robResponse: List<IncludedStudiesAnswersService.QuestionWithAnswer> = robQuestions.map { q ->
            IncludedStudiesAnswersService.QuestionWithAnswer(
                questionId = q.questionId,
                code = q.code,
                type = q.questionType,
                description = q.description,
                answer = robAnswers[q.questionId]
            )
        }


        val response = IncludedStudiesAnswersService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            studyReviewId = request.studyReviewId,
            year = studyReviewDto.year,
            includedBy = studyReviewDto.criteria,
            extractionQuestions = extractionResponse,
            robQuestions = robResponse
        )

        presenter.prepareSuccessView(response)
    }
}
